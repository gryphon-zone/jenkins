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

import jenkins.install.InstallState
import jenkins.install.InstallStateFilter
import jenkins.model.Jenkins

import javax.inject.Provider
import java.util.logging.Logger

final Logger log = Logger.getLogger(getClass().getName())

class SkipPluginInstallStepStateFilter extends InstallStateFilter {

    private final Logger log = Logger.getLogger(getClass().getName())

    private static String name(InstallState state) {
        return state == null ? null : state.name()
    }

    @Override
    InstallState getNextInstallState(InstallState current, Provider<InstallState> proceed) {
        final InstallState proposedNext = proceed.get()

        InstallState next

        /*
         * At the time of writing, the state transition during a new installation is:
         *
         * 1) INITIAL_SECURITY_SETUP
         * 2) NEW
         * 3) INITIAL_PLUGINS_INSTALLING
         * 4) CREATE_ADMIN_USER
         *
         * The InstallStateFilter isn't called for the transition from NEW -> INITIAL_PLUGINS_INSTALLING, and
         * INITIAL_PLUGINS_INSTALLING is the state immediately following the user going through the plugin installation
         * wizard, which means that the only way to bypass the plugin installation section is to transition directly
         * from INITIAL_SECURITY_SETUP -> CREATE_ADMIN_USER.
         *
         * Ideally the state transition would look like the following, with the filter being called for each:
         *
         * 1) INITIAL_SECURITY_SETUP
         * 2) NEW
         * 3) INITIAL_PLUGINS_SELECTION
         * 4) INITIAL_PLUGINS_INSTALLING
         * 5) CREATE_ADMIN_USER
         *
         * so that the filter could move directly from NEW -> CREATE_ADMIN_USER without interfering with other steps.
         *
         * The only thing that _appears_ to happen during the 'NEW' state is installation of plugins via the wizard,
         * so bypassing it appears to be safe currently, however that could change the future and this hack could
         * cause the installer to break
         */
        if (Objects.equals(current.name(), InstallState.INITIAL_SECURITY_SETUP.name())) {
            next = InstallState.CREATE_ADMIN_USER
        } else {
            next = proposedNext
        }

        if (next != proposedNext) {
            log.warning("Current state: '${name(current)}', default next state: '${name(proposedNext)}', overridden to: '${name(next)}'")
        }

        return next
    }
}

Jenkins instance = Jenkins.get()

log.info("Installing custom ${InstallStateFilter.class.simpleName} '${SkipPluginInstallStepStateFilter.class.simpleName}'")

//noinspection GrDeprecatedAPIUsage
instance.getExtensionList(InstallStateFilter.class).add(new SkipPluginInstallStepStateFilter())

instance.save()
