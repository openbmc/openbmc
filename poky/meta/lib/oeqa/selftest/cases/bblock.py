#
# Copyright (c) 2023 BayLibre, SAS
# Author: Julien Stepahn <jstephan@baylibre.com>
#
# SPDX-License-Identifier: GPL-2.0-only
#

import os
import re
import bb.tinfoil

import oeqa.utils.ftools as ftools
from oeqa.utils.commands import runCmd, get_bb_var, get_bb_vars, bitbake

from oeqa.selftest.case import OESelftestTestCase


class BBLock(OESelftestTestCase):
    @classmethod
    def setUpClass(cls):
        super(BBLock, cls).setUpClass()
        cls.lockfile = cls.builddir + "/conf/bblock.conf"

    def unlock_recipes(self, recipes=None, tasks=None):
        cmd = "bblock -r "
        if recipes:
            cmd += " ".join(recipes)
        if tasks:
            cmd += " -t " + ",".join(tasks)
        result = runCmd(cmd)

        if recipes:
            # ensure all signatures are removed from lockfile
            contents = ftools.read_file(self.lockfile)
            for recipe in recipes:
                for task in tasks:
                    find_in_contents = re.search(
                        'SIGGEN_LOCKEDSIGS_.+\s\+=\s"%s:%s:.*"' % (recipe, task),
                        contents,
                    )
                    self.assertFalse(
                        find_in_contents,
                        msg="%s:%s should not be present into bblock.conf anymore"
                        % (recipe, task),
                    )
                self.assertExists(self.lockfile)
        else:
            self.assertNotExists(self.lockfile)

    def lock_recipes(self, recipes, tasks=None):
        cmd = "bblock " + " ".join(recipes)
        if tasks:
            cmd += " -t " + ",".join(tasks)

        result = runCmd(cmd)

        self.assertExists(self.lockfile)

        # ensure all signatures are added to lockfile
        contents = ftools.read_file(self.lockfile)
        for recipe in recipes:
            if tasks:
                for task in tasks:
                    find_in_contents = re.search(
                        'SIGGEN_LOCKEDSIGS_.+\s\+=\s"%s:%s:.*"' % (recipe, task),
                        contents,
                    )
                    self.assertTrue(
                        find_in_contents,
                        msg="%s:%s was not added into bblock.conf. bblock output: %s"
                        % (recipe, task, result.output),
                    )

    def modify_tasks(self, recipes, tasks):
        task_append = ""
        for recipe in recipes:
            bb_vars = get_bb_vars(["PV"], recipe)
            recipe_pv = bb_vars["PV"]
            recipe_append_file = recipe + "_" + recipe_pv + ".bbappend"

            os.mkdir(os.path.join(self.testlayer_path, "recipes-test", recipe))
            recipe_append_path = os.path.join(
                self.testlayer_path, "recipes-test", recipe, recipe_append_file
            )

            for task in tasks:
                task_append += "%s:append() {\n#modify task hash \n}\n" % task
            ftools.write_file(recipe_append_path, task_append)
            self.add_command_to_tearDown(
                "rm -rf %s" % os.path.join(self.testlayer_path, "recipes-test", recipe)
            )

    def test_lock_single_recipe_single_task(self):
        recipes = ["quilt"]
        tasks = ["do_compile"]
        self._run_test(recipes, tasks)

    def test_lock_single_recipe_multiple_tasks(self):
        recipes = ["quilt"]
        tasks = ["do_compile", "do_install"]
        self._run_test(recipes, tasks)

    def test_lock_single_recipe_all_tasks(self):
        recipes = ["quilt"]
        self._run_test(recipes, None)

    def test_lock_multiple_recipe_single_task(self):
        recipes = ["quilt", "bc"]
        tasks = ["do_compile"]
        self._run_test(recipes, tasks)

    def test_lock_architecture_specific(self):
        # unlock all recipes and ensure no bblock.conf file exist
        self.unlock_recipes()

        recipes = ["quilt"]
        tasks = ["do_compile"]

        # lock quilt's do_compile task for another machine
        if self.td["MACHINE"] == "qemux86-64":
            machine = "qemuarm"
        else:
            machine = "qemux86-64"

        self.write_config('MACHINE = "%s"\n' % machine)

        self.lock_recipes(recipes, tasks)

        self.write_config('MACHINE = "%s"\n' % self.td["MACHINE"])
        # modify quilt's do_compile task
        self.modify_tasks(recipes, tasks)

        # build quilt using the default machine
        # No Note/Warning should be emitted since sig is locked for another machine
        # (quilt package is architecture dependant)
        info_message = "NOTE: The following recipes have locked tasks: " + recipes[0]
        warn_message = "The %s:%s sig is computed to be" % (recipes[0], tasks[0])
        result = bitbake(recipes[0] + " -n")
        self.assertNotIn(info_message, result.output)
        self.assertNotIn(warn_message, result.output)

        # unlock all recipes
        self.unlock_recipes()

    def _run_test(self, recipes, tasks=None):
        # unlock all recipes and ensure no bblock.conf file exist
        self.unlock_recipes()

        self.write_config('BB_SIGNATURE_HANDLER = "OEBasicHash"')

        # lock tasks for recipes
        result = self.lock_recipes(recipes, tasks)

        if not tasks:
            tasks = []
            result = bitbake("-c listtasks " + recipes[0])
            with bb.tinfoil.Tinfoil() as tinfoil:
                tinfoil.prepare(config_only=False, quiet=2)
                d = tinfoil.parse_recipe(recipes[0])

                for line in result.output.splitlines():
                    if line.startswith("do_"):
                        task = line.split()[0]
                        if "setscene" in task:
                            continue
                        if d.getVarFlag(task, "nostamp"):
                            continue
                        tasks.append(task)

        # build recipes. At this stage we should have a Note about recipes
        # having locked task's sig, but no warning since sig still match
        info_message = "NOTE: The following recipes have locked tasks: " + " ".join(
            recipes
        )
        for recipe in recipes:
            result = bitbake(recipe + " -n")
            self.assertIn(info_message, result.output)
            for task in tasks:
                warn_message = "The %s:%s sig is computed to be" % (recipe, task)
                self.assertNotIn(warn_message, result.output)

        # modify all tasks that are locked to trigger a sig change then build the recipes
        # at this stage we should have a Note as before, but also a Warning for all
        # locked tasks indicating the sig mismatch
        self.modify_tasks(recipes, tasks)
        for recipe in recipes:
            result = bitbake(recipe + " -n")
            self.assertIn(info_message, result.output)
            for task in tasks:
                warn_message = "The %s:%s sig is computed to be" % (recipe, task)
                self.assertIn(warn_message, result.output)

        # unlock all tasks and rebuild, no more Note/Warning should remain
        self.unlock_recipes(recipes, tasks)
        for recipe in recipes:
            result = bitbake(recipe + " -n")
            self.assertNotIn(info_message, result.output)
            for task in tasks:
                warn_message = "The %s:%s sig is computed to be" % (recipe, task)
                self.assertNotIn(warn_message, result.output)

        # unlock all recipes
        self.unlock_recipes()
