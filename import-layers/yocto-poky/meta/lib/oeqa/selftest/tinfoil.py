import unittest
import os
import re
import bb.tinfoil

from oeqa.selftest.base import oeSelfTest
from oeqa.utils.commands import runCmd
from oeqa.utils.decorators import testcase

class TinfoilTests(oeSelfTest):
    """ Basic tests for the tinfoil API """

    @testcase(1568)
    def test_getvar(self):
        with bb.tinfoil.Tinfoil() as tinfoil:
            tinfoil.prepare(True)
            machine = tinfoil.config_data.getVar('MACHINE')
            if not machine:
                self.fail('Unable to get MACHINE value - returned %s' % machine)

    @testcase(1569)
    def test_expand(self):
        with bb.tinfoil.Tinfoil() as tinfoil:
            tinfoil.prepare(True)
            expr = '${@os.getpid()}'
            pid = tinfoil.config_data.expand(expr)
            if not pid:
                self.fail('Unable to expand "%s" - returned %s' % (expr, pid))

    @testcase(1570)
    def test_getvar_bb_origenv(self):
        with bb.tinfoil.Tinfoil() as tinfoil:
            tinfoil.prepare(True)
            origenv = tinfoil.config_data.getVar('BB_ORIGENV', False)
            if not origenv:
                self.fail('Unable to get BB_ORIGENV value - returned %s' % origenv)
            self.assertEqual(origenv.getVar('HOME', False), os.environ['HOME'])

    @testcase(1571)
    def test_parse_recipe(self):
        with bb.tinfoil.Tinfoil() as tinfoil:
            tinfoil.prepare(config_only=False, quiet=2)
            testrecipe = 'mdadm'
            best = tinfoil.find_best_provider(testrecipe)
            if not best:
                self.fail('Unable to find recipe providing %s' % testrecipe)
            rd = tinfoil.parse_recipe_file(best[3])
            self.assertEqual(testrecipe, rd.getVar('PN'))

    @testcase(1572)
    def test_parse_recipe_copy_expand(self):
        with bb.tinfoil.Tinfoil() as tinfoil:
            tinfoil.prepare(config_only=False, quiet=2)
            testrecipe = 'mdadm'
            best = tinfoil.find_best_provider(testrecipe)
            if not best:
                self.fail('Unable to find recipe providing %s' % testrecipe)
            rd = tinfoil.parse_recipe_file(best[3])
            # Check we can get variable values
            self.assertEqual(testrecipe, rd.getVar('PN'))
            # Check that expanding a value that includes a variable reference works
            self.assertEqual(testrecipe, rd.getVar('BPN'))
            # Now check that changing the referenced variable's value in a copy gives that
            # value when expanding
            localdata = bb.data.createCopy(rd)
            localdata.setVar('PN', 'hello')
            self.assertEqual('hello', localdata.getVar('BPN'))

    @testcase(1573)
    def test_parse_recipe_initial_datastore(self):
        with bb.tinfoil.Tinfoil() as tinfoil:
            tinfoil.prepare(config_only=False, quiet=2)
            testrecipe = 'mdadm'
            best = tinfoil.find_best_provider(testrecipe)
            if not best:
                self.fail('Unable to find recipe providing %s' % testrecipe)
            dcopy = bb.data.createCopy(tinfoil.config_data)
            dcopy.setVar('MYVARIABLE', 'somevalue')
            rd = tinfoil.parse_recipe_file(best[3], config_data=dcopy)
            # Check we can get variable values
            self.assertEqual('somevalue', rd.getVar('MYVARIABLE'))

    @testcase(1574)
    def test_list_recipes(self):
        with bb.tinfoil.Tinfoil() as tinfoil:
            tinfoil.prepare(config_only=False, quiet=2)
            # Check pkg_pn
            checkpns = ['tar', 'automake', 'coreutils', 'm4-native', 'nativesdk-gcc']
            pkg_pn = tinfoil.cooker.recipecaches[''].pkg_pn
            for pn in checkpns:
                self.assertIn(pn, pkg_pn)
            # Check pkg_fn
            checkfns = {'nativesdk-gcc': '^virtual:nativesdk:.*', 'coreutils': '.*/coreutils_.*.bb'}
            for fn, pn in tinfoil.cooker.recipecaches[''].pkg_fn.items():
                if pn in checkpns:
                    if pn in checkfns:
                        self.assertTrue(re.match(checkfns[pn], fn), 'Entry for %s: %s did not match %s' % (pn, fn, checkfns[pn]))
                    checkpns.remove(pn)
            if checkpns:
                self.fail('Unable to find pkg_fn entries for: %s' % ', '.join(checkpns))

    @testcase(1575)
    def test_wait_event(self):
        with bb.tinfoil.Tinfoil() as tinfoil:
            tinfoil.prepare(config_only=True)
            # Need to drain events otherwise events that will be masked will still be in the queue
            while tinfoil.wait_event(0.25):
                pass
            tinfoil.set_event_mask(['bb.event.FilesMatchingFound', 'bb.command.CommandCompleted'])
            pattern = 'conf'
            res = tinfoil.run_command('findFilesMatchingInDir', pattern, 'conf/machine')
            self.assertTrue(res)

            eventreceived = False
            waitcount = 5
            while waitcount > 0:
                event = tinfoil.wait_event(1)
                if event:
                    if isinstance(event, bb.command.CommandCompleted):
                        break
                    elif isinstance(event, bb.event.FilesMatchingFound):
                        self.assertEqual(pattern, event._pattern)
                        self.assertIn('qemuarm.conf', event._matches)
                        eventreceived = True
                    else:
                        self.fail('Unexpected event: %s' % event)

                waitcount = waitcount - 1

            self.assertNotEqual(waitcount, 0, 'Timed out waiting for CommandCompleted event from bitbake server')
            self.assertTrue(eventreceived, 'Did not receive FilesMatchingFound event from bitbake server')

    @testcase(1576)
    def test_setvariable_clean(self):
        # First check that setVariable affects the datastore
        with bb.tinfoil.Tinfoil() as tinfoil:
            tinfoil.prepare(config_only=True)
            tinfoil.run_command('setVariable', 'TESTVAR', 'specialvalue')
            self.assertEqual(tinfoil.config_data.getVar('TESTVAR'), 'specialvalue', 'Value set using setVariable is not reflected in client-side getVar()')

        # Now check that the setVariable's effects are no longer present
        # (this may legitimately break in future if we stop reinitialising
        # the datastore, in which case we'll have to reconsider use of
        # setVariable entirely)
        with bb.tinfoil.Tinfoil() as tinfoil:
            tinfoil.prepare(config_only=True)
            self.assertNotEqual(tinfoil.config_data.getVar('TESTVAR'), 'specialvalue', 'Value set using setVariable is still present!')

        # Now check that setVar on the main datastore works (uses setVariable internally)
        with bb.tinfoil.Tinfoil() as tinfoil:
            tinfoil.prepare(config_only=True)
            tinfoil.config_data.setVar('TESTVAR', 'specialvalue')
            value = tinfoil.run_command('getVariable', 'TESTVAR')
            self.assertEqual(value, 'specialvalue', 'Value set using config_data.setVar() is not reflected in config_data.getVar()')

    def test_datastore_operations(self):
        with bb.tinfoil.Tinfoil() as tinfoil:
            tinfoil.prepare(config_only=True)
            # Test setVarFlag() / getVarFlag()
            tinfoil.config_data.setVarFlag('TESTVAR', 'flagname', 'flagval')
            value = tinfoil.config_data.getVarFlag('TESTVAR', 'flagname')
            self.assertEqual(value, 'flagval', 'Value set using config_data.setVarFlag() is not reflected in config_data.getVarFlag()')
            # Test delVarFlag()
            tinfoil.config_data.setVarFlag('TESTVAR', 'otherflag', 'othervalue')
            tinfoil.config_data.delVarFlag('TESTVAR', 'flagname')
            value = tinfoil.config_data.getVarFlag('TESTVAR', 'flagname')
            self.assertEqual(value, None, 'Varflag deleted using config_data.delVarFlag() is not reflected in config_data.getVarFlag()')
            value = tinfoil.config_data.getVarFlag('TESTVAR', 'otherflag')
            self.assertEqual(value, 'othervalue', 'Varflag deleted using config_data.delVarFlag() caused unrelated flag to be removed')
            # Test delVar()
            tinfoil.config_data.setVar('TESTVAR', 'varvalue')
            value = tinfoil.config_data.getVar('TESTVAR')
            self.assertEqual(value, 'varvalue', 'Value set using config_data.setVar() is not reflected in config_data.getVar()')
            tinfoil.config_data.delVar('TESTVAR')
            value = tinfoil.config_data.getVar('TESTVAR')
            self.assertEqual(value, None, 'Variable deleted using config_data.delVar() appears to still have a value')
            # Test renameVar()
            tinfoil.config_data.setVar('TESTVAROLD', 'origvalue')
            tinfoil.config_data.renameVar('TESTVAROLD', 'TESTVARNEW')
            value = tinfoil.config_data.getVar('TESTVAROLD')
            self.assertEqual(value, None, 'Variable renamed using config_data.renameVar() still seems to exist')
            value = tinfoil.config_data.getVar('TESTVARNEW')
            self.assertEqual(value, 'origvalue', 'Variable renamed using config_data.renameVar() does not appear with new name')
            # Test overrides
            tinfoil.config_data.setVar('TESTVAR', 'original')
            tinfoil.config_data.setVar('TESTVAR_overrideone', 'one')
            tinfoil.config_data.setVar('TESTVAR_overridetwo', 'two')
            tinfoil.config_data.appendVar('OVERRIDES', ':overrideone')
            value = tinfoil.config_data.getVar('TESTVAR')
            self.assertEqual(value, 'one', 'Variable overrides not functioning correctly')
