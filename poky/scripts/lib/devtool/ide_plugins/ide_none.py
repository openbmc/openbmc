#
# Copyright (C) 2023-2024 Siemens AG
#
# SPDX-License-Identifier: GPL-2.0-only
#
"""Devtool ide-sdk generic IDE plugin"""

import os
import logging
from devtool.ide_plugins import IdeBase, GdbCrossConfig

logger = logging.getLogger('devtool')


class IdeNone(IdeBase):
    """Generate some generic helpers for other IDEs

    Modified recipe mode:
    Generate some helper scripts for remote debugging with GDB

    Shared sysroot mode:
    A wrapper for bitbake meta-ide-support and bitbake build-sysroots
    """

    def __init__(self):
        super().__init__()

    def setup_shared_sysroots(self, shared_env):
        real_multimach_target_sys = shared_env.ide_support.real_multimach_target_sys
        deploy_dir_image = shared_env.ide_support.deploy_dir_image
        env_script = os.path.join(
            deploy_dir_image, 'environment-setup-' + real_multimach_target_sys)
        logger.info(
            "To use this SDK please source this: %s" % env_script)

    def setup_modified_recipe(self, args, image_recipe, modified_recipe):
        """generate some helper scripts and config files

        - Execute the do_install task
        - Execute devtool deploy-target
        - Generate a gdbinit file per executable
        - Generate the oe-scripts sym-link
        """
        script_path = modified_recipe.gen_install_deploy_script(args)
        logger.info("Created: %s" % script_path)

        self.initialize_gdb_cross_configs(image_recipe, modified_recipe)

        IdeBase.gen_oe_scrtips_sym_link(modified_recipe)


def register_ide_plugin(ide_plugins):
    ide_plugins['none'] = IdeNone
