#
# SPDX-License-Identifier: MIT
#

import os
import re
import time
import logging
import bb.tinfoil

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import runCmd

class TinfoilTests(OESelftestTestCase):
    """ Basic tests for the tinfoil API """

    def test_getvar(self):
        with bb.tinfoil.Tinfoil() as tinfoil:
            tinfoil.prepare(True)
            machine = tinfoil.config_data.getVar('MACHINE')
            if not machine:
                self.fail('Unable to get MACHINE value - returned %s' % machine)

    def test_expand(self):
        with bb.tinfoil.Tinfoil() as tinfoil:
            tinfoil.prepare(True)
            expr = '${@os.getpid()}'
            pid = tinfoil.config_data.expand(expr)
            if not pid:
                self.fail('Unable to expand "%s" - returned %s' % (expr, pid))

    def test_getvar_bb_origenv(self):
        with bb.tinfoil.Tinfoil() as tinfoil:
            tinfoil.prepare(True)
            origenv = tinfoil.config_data.getVar('BB_ORIGENV', False)
            if not origenv:
                self.fail('Unable to get BB_ORIGENV value - returned %s' % origenv)
            self.assertEqual(origenv.getVar('HOME', False), os.environ['HOME'])

    def test_parse_recipe(self):
        with bb.tinfoil.Tinfoil() as tinfoil:
            tinfoil.prepare(config_only=False, quiet=2)
            testrecipe = 'mdadm'
            best = tinfoil.find_best_provider(testrecipe)
            if not best:
                self.fail('Unable to find recipe providing %s' % testrecipe)
            rd = tinfoil.parse_recipe_file(best[3])
            self.assertEqual(testrecipe, rd.getVar('PN'))

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

    def test_wait_event(self):
        with bb.tinfoil.Tinfoil() as tinfoil:
            tinfoil.prepare(config_only=True)

            tinfoil.set_event_mask(['bb.event.FilesMatchingFound', 'bb.command.CommandCompleted'])

            # Need to drain events otherwise events that were masked may still be in the queue
            while tinfoil.wait_event():
                pass

            pattern = 'conf'
            res = tinfoil.run_command('findFilesMatchingInDir', pattern, 'conf/machine')
            self.assertTrue(res)

            eventreceived = False
            commandcomplete = False
            start = time.time()
            # Wait for maximum 60s in total so we'd detect spurious heartbeat events for example
            # The test is IO load sensitive too
            while (not (eventreceived == True and commandcomplete == True) 
                    and (time.time() - start < 60)):
                # if we received both events (on let's say a good day), we are done  
                event = tinfoil.wait_event(1)
                if event:
                    if isinstance(event, bb.command.CommandCompleted):
                        commandcomplete = True
                    elif isinstance(event, bb.event.FilesMatchingFound):
                        self.assertEqual(pattern, event._pattern)
                        self.assertIn('qemuarm.conf', event._matches)
                        eventreceived = True
                    elif isinstance(event, logging.LogRecord):
                        continue
                    else:
                        self.fail('Unexpected event: %s' % event)

            self.assertTrue(commandcomplete, 'Timed out waiting for CommandCompleted event from bitbake server')
            self.assertTrue(eventreceived, 'Did not receive FilesMatchingFound event from bitbake server')

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

    def test_variable_history(self):
        # Basic test to ensure that variable history works when tracking=True
        with bb.tinfoil.Tinfoil(tracking=True) as tinfoil:
            tinfoil.prepare(config_only=False, quiet=2)
            # Note that _tracking for any datastore we get will be
            # false here, that's currently expected - so we can't check
            # for that
            history = tinfoil.config_data.varhistory.variable('DL_DIR')
            for entry in history:
                if entry['file'].endswith('/bitbake.conf'):
                    if entry['op'] in ['set', 'set?']:
                        break
            else:
                self.fail('Did not find history entry setting DL_DIR in bitbake.conf. History: %s' % history)
            # Check it works for recipes as well
            testrecipe = 'zlib'
            rd = tinfoil.parse_recipe(testrecipe)
            history = rd.varhistory.variable('LICENSE')
            bbfound = -1
            recipefound = -1
            for i, entry in enumerate(history):
                if entry['file'].endswith('/bitbake.conf'):
                    if entry['detail'] == 'INVALID' and entry['op'] in ['set', 'set?']:
                        bbfound = i
                elif entry['file'].endswith('.bb'):
                    if entry['op'] == 'set':
                        recipefound = i
            if bbfound == -1:
                self.fail('Did not find history entry setting LICENSE in bitbake.conf parsing %s recipe. History: %s' % (testrecipe, history))
            if recipefound == -1:
                self.fail('Did not find history entry setting LICENSE in %s recipe. History: %s' % (testrecipe, history))
            if bbfound > recipefound:
                self.fail('History entry setting LICENSE in %s recipe and in bitbake.conf in wrong order. History: %s' % (testrecipe, history))
