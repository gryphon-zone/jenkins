/*
 * Copyright 2019-2019 Gryphon Zone
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import com.michelin.cio.hudson.plugins.rolestrategy.RoleBasedAuthorizationStrategy
import groovy.transform.Field
import hudson.security.HudsonPrivateSecurityRealm
import jenkins.install.InstallState
import jenkins.install.InstallStateFilter
import jenkins.model.Jenkins
import net.sf.json.JSONObject

import javax.inject.Provider
import java.util.logging.Level
import java.util.logging.Logger

@Field
final Logger log = Logger.getLogger(getClass().getName())

@Field
final String adminUsername = 'admin'

@Field
Jenkins instance = Jenkins.get()

def configureInstanceSecurity() {

    try {
        // make sure admin user exists
        instance.getSecurityRealm().loadUserByUsername(adminUsername)
    } catch (Exception e) {
        log.log(Level.SEVERE, "Unable to get user '${adminUsername}', not configuring instance security", e)
        return
    }

    if (instance.getInstallState().isSetupComplete()) {
        log.info("Jenkins instance has already been set up and has an admin user, not configuring security")
        return
    }

    if (!(instance.getSecurityRealm() instanceof HudsonPrivateSecurityRealm)) {
        instance.setSecurityRealm(new HudsonPrivateSecurityRealm(false))
    }

    if (!(instance.getAuthorizationStrategy() instanceof RoleBasedAuthorizationStrategy)) {
        instance.setAuthorizationStrategy(RoleBasedAuthorizationStrategy.DESCRIPTOR.newInstance(null, new JSONObject()))
    }

    ((RoleBasedAuthorizationStrategy) instance.getAuthorizationStrategy()).doAssignRole(RoleBasedAuthorizationStrategy.GLOBAL, "admin", adminUsername)

    instance.save()
}

class SkipAdminUserSetupInstallStateFilter extends InstallStateFilter {

    private final Logger log = Logger.getLogger(SkipAdminUserSetupInstallStateFilter.class.getName())

    private final Closure configureInstanceSecurity

    private static String name(InstallState state) {
        return state == null ? null : state.name()
    }

    SkipAdminUserSetupInstallStateFilter(Closure configureInstanceSecurity) {
        this.configureInstanceSecurity = configureInstanceSecurity
    }

    @Override
    InstallState getNextInstallState(InstallState current, Provider<InstallState> proceed) {
        final InstallState proposedNext = proceed.get()

        InstallState next

        if (Objects.equals(name(proposedNext), name(InstallState.CREATE_ADMIN_USER))) {
            configureInstanceSecurity()
            next = InstallState.CONFIGURE_INSTANCE
        } else {
            next = proposedNext
        }

        if (Objects.equals(name(next), name(InstallState.RUNNING))) {

            // since we skipped setting up the admin user, manually delete the default password file
            // to prevent it from being used to gain access to the system.
            try {
                Jenkins.get().getSetupWizard().getInitialAdminPasswordFile().delete()
            } catch (Exception e) {
                log.log(Level.WARNING, "Failed to delete admin password file", e)
            }
        }

        if (next != proposedNext) {
            log.warning("Current state: '${name(current)}', default next state: '${name(proposedNext)}', overridden to: '${name(next)}'")
        }

        return next
    }
}

log.info("Installing custom ${InstallStateFilter.class.simpleName} '${SkipAdminUserSetupInstallStateFilter.class.simpleName}'")

//noinspection GrDeprecatedAPIUsage
instance.getExtensionList(InstallStateFilter.class).add(new SkipAdminUserSetupInstallStateFilter({
    configureInstanceSecurity()
}))

instance.save()
