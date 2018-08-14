# Copyright (C) 2017 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

import unittest

from checklayer import LayerType, get_signatures, check_command, get_depgraph
from checklayer.case import OECheckLayerTestCase

class BSPCheckLayer(OECheckLayerTestCase):
    @classmethod
    def setUpClass(self):
        if self.tc.layer['type'] != LayerType.BSP:
            raise unittest.SkipTest("BSPCheckLayer: Layer %s isn't BSP one." %\
                self.tc.layer['name'])

    def test_bsp_defines_machines(self):
        self.assertTrue(self.tc.layer['conf']['machines'], 
                "Layer is BSP but doesn't defines machines.")

    def test_bsp_no_set_machine(self):
        from oeqa.utils.commands import get_bb_var

        machine = get_bb_var('MACHINE')
        self.assertEqual(self.td['bbvars']['MACHINE'], machine,
                msg="Layer %s modified machine %s -> %s" % \
                    (self.tc.layer['name'], self.td['bbvars']['MACHINE'], machine))


    def test_machine_world(self):
        '''
        "bitbake world" is expected to work regardless which machine is selected.
        BSP layers sometimes break that by enabling a recipe for a certain machine
        without checking whether that recipe actually can be built in the current
        distro configuration (for example, OpenGL might not enabled).

        This test iterates over all machines. It would be nicer to instantiate
        it once per machine. It merely checks for errors during parse
        time. It does not actually attempt to build anything.
        '''

        if not self.td['machines']:
            self.skipTest('No machines set with --machines.')
        msg = []
        for machine in self.td['machines']:
            # In contrast to test_machine_signatures() below, errors are fatal here.
            try:
                get_signatures(self.td['builddir'], failsafe=False, machine=machine)
            except RuntimeError as ex:
                msg.append(str(ex))
        if msg:
            msg.insert(0, 'The following machines broke a world build:')
            self.fail('\n'.join(msg))

    def test_machine_signatures(self):
        '''
        Selecting a machine may only affect the signature of tasks that are specific
        to that machine. In other words, when MACHINE=A and MACHINE=B share a recipe
        foo and the output of foo, then both machine configurations must build foo
        in exactly the same way. Otherwise it is not possible to use both machines
        in the same distribution.

        This criteria can only be tested by testing different machines in combination,
        i.e. one main layer, potentially several additional BSP layers and an explicit
        choice of machines:
        yocto-check-layer --additional-layers .../meta-intel --machines intel-corei7-64 imx6slevk -- .../meta-freescale
        '''

        if not self.td['machines']:
            self.skipTest('No machines set with --machines.')

        # Collect signatures for all machines that we are testing
        # and merge that into a hash:
        # tune -> task -> signature -> list of machines with that combination
        #
        # It is an error if any tune/task pair has more than one signature,
        # because that implies that the machines that caused those different
        # signatures do not agree on how to execute the task.
        tunes = {}
        # Preserve ordering of machines as chosen by the user.
        for machine in self.td['machines']:
            curr_sigs, tune2tasks = get_signatures(self.td['builddir'], failsafe=True, machine=machine)
            # Invert the tune -> [tasks] mapping.
            tasks2tune = {}
            for tune, tasks in tune2tasks.items():
                for task in tasks:
                    tasks2tune[task] = tune
            for task, sighash in curr_sigs.items():
                tunes.setdefault(tasks2tune[task], {}).setdefault(task, {}).setdefault(sighash, []).append(machine)

        msg = []
        pruned = 0
        last_line_key = None
        # do_fetch, do_unpack, ..., do_build
        taskname_list = []
        if tunes:
            # The output below is most useful when we start with tasks that are at
            # the bottom of the dependency chain, i.e. those that run first. If
            # those tasks differ, the rest also does.
            #
            # To get an ordering of tasks, we do a topological sort of the entire
            # depgraph for the base configuration, then on-the-fly flatten that list by stripping
            # out the recipe names and removing duplicates. The base configuration
            # is not necessarily representative, but should be close enough. Tasks
            # that were not encountered get a default priority.
            depgraph = get_depgraph()
            depends = depgraph['tdepends']
            WHITE = 1
            GRAY = 2
            BLACK = 3
            color = {}
            found = set()
            def visit(task):
                color[task] = GRAY
                for dep in depends.get(task, ()):
                    if color.setdefault(dep, WHITE) == WHITE:
                        visit(dep)
                color[task] = BLACK
                pn, taskname = task.rsplit('.', 1)
                if taskname not in found:
                    taskname_list.append(taskname)
                    found.add(taskname)
            for task in depends.keys():
                if color.setdefault(task, WHITE) == WHITE:
                    visit(task)

        taskname_order = dict([(task, index) for index, task in enumerate(taskname_list) ])
        def task_key(task):
            pn, taskname = task.rsplit(':', 1)
            return (pn, taskname_order.get(taskname, len(taskname_list)), taskname)

        for tune in sorted(tunes.keys()):
            tasks = tunes[tune]
            # As for test_signatures it would be nicer to sort tasks
            # by dependencies here, but that is harder because we have
            # to report on tasks from different machines, which might
            # have different dependencies. We resort to pruning the
            # output by reporting only one task per recipe if the set
            # of machines matches.
            #
            # "bitbake-diffsigs -t -s" is intelligent enough to print
            # diffs recursively, so often it does not matter that much
            # if we don't pick the underlying difference
            # here. However, sometimes recursion fails
            # (https://bugzilla.yoctoproject.org/show_bug.cgi?id=6428).
            #
            # To mitigate that a bit, we use a hard-coded ordering of
            # tasks that represents how they normally run and prefer
            # to print the ones that run first.
            for task in sorted(tasks.keys(), key=task_key):
                signatures = tasks[task]
                # do_build can be ignored: it is know to have
                # different signatures in some cases, for example in
                # the allarch ca-certificates due to RDEPENDS=openssl.
                # That particular dependency is whitelisted via
                # SIGGEN_EXCLUDE_SAFE_RECIPE_DEPS, but still shows up
                # in the sstate signature hash because filtering it
                # out would be hard and running do_build multiple
                # times doesn't really matter.
                if len(signatures.keys()) > 1 and \
                   not task.endswith(':do_build'):
                    # Error!
                    #
                    # Sort signatures by machines, because the hex values don't mean anything.
                    # => all-arch adwaita-icon-theme:do_build: 1234... (beaglebone, qemux86) != abcdf... (qemux86-64)
                    #
                    # Skip the line if it is covered already by the predecessor (same pn, same sets of machines).
                    pn, taskname = task.rsplit(':', 1)
                    next_line_key = (pn, sorted(signatures.values()))
                    if next_line_key != last_line_key:
                        line = '   %s %s: ' % (tune, task)
                        line += ' != '.join(['%s (%s)' % (signature, ', '.join([m for m in signatures[signature]])) for
                                             signature in sorted(signatures.keys(), key=lambda s: signatures[s])])
                        last_line_key = next_line_key
                        msg.append(line)
                        # Randomly pick two mismatched signatures and remember how to invoke
                        # bitbake-diffsigs for them.
                        iterator = iter(signatures.items())
                        a = next(iterator)
                        b = next(iterator)
                        diffsig_machines = '(%s) != (%s)' % (', '.join(a[1]), ', '.join(b[1]))
                        diffsig_params = '-t %s %s -s %s %s' % (pn, taskname, a[0], b[0])
                    else:
                        pruned += 1

        if msg:
            msg.insert(0, 'The machines have conflicting signatures for some shared tasks:')
            if pruned > 0:
                msg.append('')
                msg.append('%d tasks where not listed because some other task of the recipe already differed.' % pruned)
                msg.append('It is likely that differences from different recipes also have the same root cause.')
            msg.append('')
            # Explain how to investigate...
            msg.append('To investigate, run bitbake-diffsigs -t recipename taskname -s fromsig tosig.')
            cmd = 'bitbake-diffsigs %s' % diffsig_params
            msg.append('Example: %s in the last line' % diffsig_machines)
            msg.append('Command: %s' % cmd)
            # ... and actually do it automatically for that example, but without aborting
            # when that fails.
            try:
                output = check_command('Comparing signatures failed.', cmd).decode('utf-8')
            except RuntimeError as ex:
                output = str(ex)
            msg.extend(['   ' + line for line in output.splitlines()])
            self.fail('\n'.join(msg))
