# Copyright (C) 2017-2018 Wind River Systems, Inc.
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
# See the GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

import unittest
import tempfile
import os
import bb

from layerindexlib.tests.common import LayersTest

import logging

class LayerIndexObjectsTest(LayersTest):
    def setUp(self):
        from layerindexlib import LayerIndexObj, Branch, LayerItem, LayerBranch, LayerDependency, Recipe, Machine, Distro

        LayersTest.setUp(self)

        self.index = LayerIndexObj()

        branchId = 0
        layerItemId = 0
        layerBranchId = 0
        layerDependencyId = 0
        recipeId = 0
        machineId = 0
        distroId = 0

        self.index.branches = {}
        self.index.layerItems = {}
        self.index.layerBranches = {}
        self.index.layerDependencies = {}
        self.index.recipes = {}
        self.index.machines = {}
        self.index.distros = {}

        branchId += 1
        self.index.branches[branchId] = Branch(self.index)
        self.index.branches[branchId].define_data(branchId,
                                        'test_branch', 'bb_test_branch')
        self.index.branches[branchId].lockData()

        layerItemId +=1
        self.index.layerItems[layerItemId] = LayerItem(self.index)
        self.index.layerItems[layerItemId].define_data(layerItemId,
                                        'test_layerItem', vcs_url='git://git_test_url/test_layerItem')
        self.index.layerItems[layerItemId].lockData()

        layerBranchId +=1
        self.index.layerBranches[layerBranchId] = LayerBranch(self.index)
        self.index.layerBranches[layerBranchId].define_data(layerBranchId,
                                        'test_collection', '99', layerItemId,
                                        branchId)

        recipeId += 1
        self.index.recipes[recipeId] = Recipe(self.index)
        self.index.recipes[recipeId].define_data(recipeId, 'test_git.bb',
                                        'recipes-test', 'test', 'git',
                                        layerBranchId)

        machineId += 1
        self.index.machines[machineId] = Machine(self.index)
        self.index.machines[machineId].define_data(machineId,
                                        'test_machine', 'test_machine',
                                        layerBranchId)

        distroId += 1
        self.index.distros[distroId] = Distro(self.index)
        self.index.distros[distroId].define_data(distroId,
                                        'test_distro', 'test_distro',
                                        layerBranchId)

        layerItemId +=1
        self.index.layerItems[layerItemId] = LayerItem(self.index)
        self.index.layerItems[layerItemId].define_data(layerItemId, 'test_layerItem 2',
                                        vcs_url='git://git_test_url/test_layerItem')

        layerBranchId +=1
        self.index.layerBranches[layerBranchId] = LayerBranch(self.index)
        self.index.layerBranches[layerBranchId].define_data(layerBranchId,
                                        'test_collection_2', '72', layerItemId,
                                        branchId, actual_branch='some_other_branch')

        layerDependencyId += 1
        self.index.layerDependencies[layerDependencyId] = LayerDependency(self.index)
        self.index.layerDependencies[layerDependencyId].define_data(layerDependencyId,
                                        layerBranchId, 1)

        layerDependencyId += 1
        self.index.layerDependencies[layerDependencyId] = LayerDependency(self.index)
        self.index.layerDependencies[layerDependencyId].define_data(layerDependencyId,
                                        layerBranchId, 1, required=False)

    def test_branch(self):
        branch = self.index.branches[1]
        self.assertEqual(branch.id, 1)
        self.assertEqual(branch.name, 'test_branch')
        self.assertEqual(branch.short_description, 'test_branch')
        self.assertEqual(branch.bitbake_branch, 'bb_test_branch')

    def test_layerItem(self):
        layerItem = self.index.layerItems[1]
        self.assertEqual(layerItem.id, 1)
        self.assertEqual(layerItem.name, 'test_layerItem')
        self.assertEqual(layerItem.summary, 'test_layerItem')
        self.assertEqual(layerItem.description, 'test_layerItem')
        self.assertEqual(layerItem.vcs_url, 'git://git_test_url/test_layerItem')
        self.assertEqual(layerItem.vcs_web_url, None)
        self.assertIsNone(layerItem.vcs_web_tree_base_url)
        self.assertIsNone(layerItem.vcs_web_file_base_url)
        self.assertIsNotNone(layerItem.updated)

        layerItem = self.index.layerItems[2]
        self.assertEqual(layerItem.id, 2)
        self.assertEqual(layerItem.name, 'test_layerItem 2')
        self.assertEqual(layerItem.summary, 'test_layerItem 2')
        self.assertEqual(layerItem.description, 'test_layerItem 2')
        self.assertEqual(layerItem.vcs_url, 'git://git_test_url/test_layerItem')
        self.assertIsNone(layerItem.vcs_web_url)
        self.assertIsNone(layerItem.vcs_web_tree_base_url)
        self.assertIsNone(layerItem.vcs_web_file_base_url)
        self.assertIsNotNone(layerItem.updated)

    def test_layerBranch(self):
        layerBranch = self.index.layerBranches[1]
        self.assertEqual(layerBranch.id, 1)
        self.assertEqual(layerBranch.collection, 'test_collection')
        self.assertEqual(layerBranch.version, '99')
        self.assertEqual(layerBranch.vcs_subdir, '')
        self.assertEqual(layerBranch.actual_branch, 'test_branch')
        self.assertIsNotNone(layerBranch.updated)
        self.assertEqual(layerBranch.layer_id, 1)
        self.assertEqual(layerBranch.branch_id, 1)
        self.assertEqual(layerBranch.layer, self.index.layerItems[1])
        self.assertEqual(layerBranch.branch, self.index.branches[1])

        layerBranch = self.index.layerBranches[2]
        self.assertEqual(layerBranch.id, 2)
        self.assertEqual(layerBranch.collection, 'test_collection_2')
        self.assertEqual(layerBranch.version, '72')
        self.assertEqual(layerBranch.vcs_subdir, '')
        self.assertEqual(layerBranch.actual_branch, 'some_other_branch')
        self.assertIsNotNone(layerBranch.updated)
        self.assertEqual(layerBranch.layer_id, 2)
        self.assertEqual(layerBranch.branch_id, 1)
        self.assertEqual(layerBranch.layer, self.index.layerItems[2])
        self.assertEqual(layerBranch.branch, self.index.branches[1])

    def test_layerDependency(self):
        layerDependency = self.index.layerDependencies[1]
        self.assertEqual(layerDependency.id, 1)
        self.assertEqual(layerDependency.layerbranch_id, 2)
        self.assertEqual(layerDependency.layerbranch, self.index.layerBranches[2])
        self.assertEqual(layerDependency.layer_id, 2)
        self.assertEqual(layerDependency.layer, self.index.layerItems[2])
        self.assertTrue(layerDependency.required)
        self.assertEqual(layerDependency.dependency_id, 1)
        self.assertEqual(layerDependency.dependency, self.index.layerItems[1])
        self.assertEqual(layerDependency.dependency_layerBranch, self.index.layerBranches[1])

        layerDependency = self.index.layerDependencies[2]
        self.assertEqual(layerDependency.id, 2)
        self.assertEqual(layerDependency.layerbranch_id, 2)
        self.assertEqual(layerDependency.layerbranch, self.index.layerBranches[2])
        self.assertEqual(layerDependency.layer_id, 2)
        self.assertEqual(layerDependency.layer, self.index.layerItems[2])
        self.assertFalse(layerDependency.required)
        self.assertEqual(layerDependency.dependency_id, 1)
        self.assertEqual(layerDependency.dependency, self.index.layerItems[1])
        self.assertEqual(layerDependency.dependency_layerBranch, self.index.layerBranches[1])

    def test_recipe(self):
        recipe = self.index.recipes[1]
        self.assertEqual(recipe.id, 1)
        self.assertEqual(recipe.layerbranch_id, 1)
        self.assertEqual(recipe.layerbranch, self.index.layerBranches[1])
        self.assertEqual(recipe.layer_id, 1)
        self.assertEqual(recipe.layer, self.index.layerItems[1])
        self.assertEqual(recipe.filename, 'test_git.bb')
        self.assertEqual(recipe.filepath, 'recipes-test')
        self.assertEqual(recipe.fullpath, 'recipes-test/test_git.bb')
        self.assertEqual(recipe.summary, "")
        self.assertEqual(recipe.description, "")
        self.assertEqual(recipe.section, "")
        self.assertEqual(recipe.pn, 'test')
        self.assertEqual(recipe.pv, 'git')
        self.assertEqual(recipe.license, "")
        self.assertEqual(recipe.homepage, "")
        self.assertEqual(recipe.bugtracker, "")
        self.assertEqual(recipe.provides, "")
        self.assertIsNotNone(recipe.updated)
        self.assertEqual(recipe.inherits, "")

    def test_machine(self):
        machine = self.index.machines[1]
        self.assertEqual(machine.id, 1)
        self.assertEqual(machine.layerbranch_id, 1)
        self.assertEqual(machine.layerbranch, self.index.layerBranches[1])
        self.assertEqual(machine.layer_id, 1)
        self.assertEqual(machine.layer, self.index.layerItems[1])
        self.assertEqual(machine.name, 'test_machine')
        self.assertEqual(machine.description, 'test_machine')
        self.assertIsNotNone(machine.updated)

    def test_distro(self):
        distro = self.index.distros[1]
        self.assertEqual(distro.id, 1)
        self.assertEqual(distro.layerbranch_id, 1)
        self.assertEqual(distro.layerbranch, self.index.layerBranches[1])
        self.assertEqual(distro.layer_id, 1)
        self.assertEqual(distro.layer, self.index.layerItems[1])
        self.assertEqual(distro.name, 'test_distro')
        self.assertEqual(distro.description, 'test_distro')
        self.assertIsNotNone(distro.updated)
