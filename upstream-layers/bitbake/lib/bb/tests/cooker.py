#
# BitBake Tests for cooker.py
#
# Copyright BitBake Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import unittest
import contextlib
import os
import subprocess
import sys
import tempfile
import time
import bb, bb.cooker
import re
import logging


class _BitbakeSubprocessTestCase(unittest.TestCase):
    """Common helpers for tests that run bitbake/tinfoil in a subprocess.

    Shared because every such subprocess can start a memory-resident bitbake
    server (and, if BB_HASHSERVE=auto, a hashserv) rooted at TOPDIR, and both
    must release that directory before the caller's TemporaryDirectory can be
    safely removed.
    """

    def _run_subprocess(self, cmd, env, cwd):
        proc = subprocess.run(
            cmd,
            env=env,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            universal_newlines=True,
            cwd=cwd,
        )
        if proc.returncode:
            self.fail('%s failed: %s' % (cmd, proc.stdout))
        return proc.stdout

    def _shutdown(self, builddir):
        """Wait for the bitbake server and hashserv to release builddir.

        Must run before the caller's TemporaryDirectory is removed, so it
        cannot be a tearDown().
        """
        deadline = time.monotonic() + 30
        while time.monotonic() < deadline:
            if not any(os.path.exists(os.path.join(builddir, p))
                       for p in ('hashserve.sock', 'bitbake.lock')):
                return
            time.sleep(0.5)

    @contextlib.contextmanager
    def _build_dir(self, prefix='tinfoiltest'):
        """TemporaryDirectory that also waits out _shutdown() before removal."""
        with tempfile.TemporaryDirectory(prefix=prefix) as builddir:
            try:
                yield builddir
            finally:
                self._shutdown(builddir)


class TinfoilTests(_BitbakeSubprocessTestCase):
    """Tests for the Tinfoil API that require a running bitbake server."""

    # Library directory containing bb.tinfoil
    _bblib = os.path.realpath(os.path.join(os.path.dirname(__file__), '..', '..'))
    # runqueue-tests BBPATH (provides the simple a1/b1/... test recipes)
    _runqueuetests = os.path.realpath(os.path.join(os.path.dirname(__file__), 'runqueue-tests'))

    failing_recipe = """\
python do_install() {
    bb.fatal("deliberate failure")
}
addtask install
"""

    # do_install genuinely depends on do_compile's output, unlike the
    # a1 fixture recipe's dummy stamptask() tasks.
    dependent_recipe = """\
python do_compile() {
    with open(d.expand("${TOPDIR}/compiled"), "w") as f:
        f.write("compiled")
}
addtask compile

python do_install() {
    if not os.path.exists(d.expand("${TOPDIR}/compiled")):
        bb.fatal("do_install ran without do_compile's output being present")
}
addtask install after do_compile
"""

    # Same contract as dependent_recipe, but with shell tasks, since a real
    # recipe's do_install is usually shell (e.g. install/cp under pseudo)
    # rather than a python function.
    shell_dependent_recipe = """\
do_compile() {
    echo compiled > "${TOPDIR}/compiled"
}
addtask compile

do_install() {
    if [ ! -e "${TOPDIR}/compiled" ]; then
        echo "do_install ran without do_compile's output being present" >&2
        exit 1
    fi
}
addtask install after do_compile
"""

    def _make_env(self, builddir, extra=None):
        env = os.environ.copy()
        env['PYTHONPATH'] = self._bblib + (':' + env['PYTHONPATH'] if 'PYTHONPATH' in env else '')
        env['BBPATH'] = self._runqueuetests
        env['BB_ENV_PASSTHROUGH_ADDITIONS'] = 'SSTATEVALID SLOWTASKS TOPDIR BB_HASHSERVE BB_SIGNATURE_HANDLER EXTRA_BBFILES'
        env['SSTATEVALID'] = ''
        env['SLOWTASKS'] = ''
        env['TOPDIR'] = builddir
        # TestEquivHash creates taint files so that force=True actually
        # invalidates the task hash; the default noop siggen cannot do this.
        env['BB_HASHSERVE'] = 'auto'
        env['BB_SIGNATURE_HANDLER'] = 'TestEquivHash'
        if extra:
            env.update(extra)
        return env

    def _run_script(self, builddir, script, extra=None):
        """Run script in a subprocess to isolate tinfoil's server lifecycle."""
        return self._run_subprocess([sys.executable, '-c', script],
                                    self._make_env(builddir, extra), builddir)

    def _read_tasklog(self, builddir, cleanup=True):
        tasklog = os.path.join(builddir, 'task.log')
        tasks = []
        if os.path.exists(tasklog):
            with open(tasklog) as f:
                tasks = [line.rstrip() for line in f]
            if cleanup:
                os.remove(tasklog)
        return tasks

    def test_run_prepared_task(self):
        """tinfoil.run_prepared_task() reruns one task without resolving deps.

        Uses do_install since that's the real devtool ide-sdk scenario: it
        needs pseudo and so must run via bitbake, unlike the compile step which
        the IDE invokes directly (e.g. via cmake/meson).

        Builds a1 completely so all stamps/hashes are valid, then calls
        run_prepared_task('a1', 'install') through the Python API and verifies
        that only do_install re-runs while its intra-recipe predecessors
        (fetch, unpack, patch, prepare_recipe_sysroot, configure, compile) are
        skipped.
        """
        # The script runs inside a subprocess so that tinfoil's server
        # lifecycle and environment modifications are isolated.
        script = """
import os, sys
import bb.tinfoil

builddir = os.environ['TOPDIR']
tasklog  = os.path.join(builddir, 'task.log')

with bb.tinfoil.Tinfoil() as tinfoil:
    tinfoil.prepare(quiet=2)
    # Full build so all stamps and hashes are valid.
    tinfoil.build_targets(['a1'])
    # Clear the log so only the run_prepared_task() entries are counted.
    if os.path.exists(tasklog):
        os.remove(tasklog)
    # run_prepared_task() sets force=True (taint) and calls build_file_sync
    # with the recipe file resolved via get_recipe_file(), bypassing the
    # normal runqueue dependency resolver.
    tinfoil.run_prepared_task('a1', 'install')
"""
        with self._build_dir() as builddir:
            self._run_script(builddir, script)

            tasks = self._read_tasklog(builddir)
            self.assertEqual(tasks, ['a1:install'],
                             'run_prepared_task should rerun only install, got: %s' % tasks)

    def test_run_prepared_task_unbuilt(self):
        """run_prepared_task() does not pull any dependency task into the runqueue.

        buildFileInternal()'s taskonly=True clears task_deps['parents'] for
        every task of the recipe, so do_install's intra-recipe predecessors
        (fetch, unpack, patch, ...) are never added to the runqueue at all.
        The recipe was never built here (no stamps exist for any of them), so
        this is the case that would actually catch a regression: without
        taskonly, those missing-stamp tasks would have to run to satisfy the
        runqueue, and the tasklog assertion below would show more than just
        'a1:install'.

        Whether do_install itself then succeeds or fails is a separate matter
        that does depend on the recipe: this fixture's do_install has no real
        prerequisites, so it succeeds here; test_run_prepared_task_unbuilt_dependent_fails()
        uses a recipe whose do_install does have one, and fails instead.
        """
        script = """
import bb.tinfoil

with bb.tinfoil.Tinfoil() as tinfoil:
    tinfoil.prepare(quiet=2)
    assert tinfoil.run_prepared_task('a1', 'install') is True
"""
        with self._build_dir() as builddir:
            self._run_script(builddir, script)

            tasks = self._read_tasklog(builddir)
            self.assertEqual(tasks, ['a1:install'],
                             'run_prepared_task should run no dependency task, got: %s' % tasks)

    def test_run_prepared_task_unbuilt_dependent_fails(self):
        """A task with a genuine dependency on a predecessor's output fails
        when that predecessor never ran.

        Unlike test_run_prepared_task_unbuilt()'s fixture recipe, whose tasks
        are dummy stamptask() calls with no real prerequisites,
        dependent_recipe's do_install actually needs do_compile's output.
        run_prepared_task() only skips bitbake's own dependency resolution;
        it does not make the prerequisites appear, exactly as documented:
        "everything the task consumes must already be in place".
        """
        script = """
import bb.tinfoil

with bb.tinfoil.Tinfoil() as tinfoil:
    tinfoil.prepare(quiet=2)
    assert tinfoil.run_prepared_task('dependent', 'install') is False
"""
        with tempfile.TemporaryDirectory(prefix='tinfoilrecipes') as recipes, \
             self._build_dir() as builddir:
            with open(os.path.join(recipes, 'dependent.bb'), 'w') as f:
                f.write(self.dependent_recipe)
            self._run_script(builddir, script,
                             {'EXTRA_BBFILES': '%s/*.bb' % recipes})

    def test_run_prepared_task_unbuilt_dependent_fails_shell(self):
        """Same as test_run_prepared_task_unbuilt_dependent_fails(), but with
        shell do_compile/do_install tasks instead of python ones, matching how
        a real recipe's do_install is usually written.
        """
        script = """
import bb.tinfoil

with bb.tinfoil.Tinfoil() as tinfoil:
    tinfoil.prepare(quiet=2)
    assert tinfoil.run_prepared_task('shelldependent', 'install') is False
"""
        with tempfile.TemporaryDirectory(prefix='tinfoilrecipes') as recipes, \
             self._build_dir() as builddir:
            with open(os.path.join(recipes, 'shelldependent.bb'), 'w') as f:
                f.write(self.shell_dependent_recipe)
            self._run_script(builddir, script,
                             {'EXTRA_BBFILES': '%s/*.bb' % recipes})

    def test_run_prepared_task_failure(self):
        """A failing task makes run_prepared_task() return False, not raise."""
        script = """
import bb.tinfoil

with bb.tinfoil.Tinfoil() as tinfoil:
    tinfoil.prepare(quiet=2)
    assert tinfoil.run_prepared_task('failer', 'install') is False
"""
        with tempfile.TemporaryDirectory(prefix='tinfoilrecipes') as recipes, \
             self._build_dir() as builddir:
            with open(os.path.join(recipes, 'failer.bb'), 'w') as f:
                f.write(self.failing_recipe)
            self._run_script(builddir, script,
                             {'EXTRA_BBFILES': '%s/*.bb' % recipes})

    def test_run_prepared_task_recipecache_self_heals(self):
        """A normal full parse after run_prepared_task() sees every recipe.

        run_prepared_task() -> buildFileInternal() -> parseConfiguration()
        wipes and repopulates self.recipecaches[mc]/self.collections[mc]
        for just the one recipe it builds - that has always been true of
        "bitbake -b" too. Prove this is only a transient footprint: a
        subsequent normal, full parse (as any non -b build would trigger)
        must see the complete recipe set again, not just the one recipe
        run_prepared_task() touched.
        """
        script = """
import bb.tinfoil

with bb.tinfoil.Tinfoil() as tinfoil:
    tinfoil.prepare(quiet=2)
    tinfoil.parse_recipes()
    recipes_before = tinfoil.run_command('getRecipes')
    assert len(recipes_before) > 1, 'test fixture should have more than one recipe'

    tinfoil.run_prepared_task('a1', 'install')

    tinfoil.parse_recipes()
    recipes_after = tinfoil.run_command('getRecipes')
    assert len(recipes_after) == len(recipes_before), (
        'recipe cache not fully restored after run_prepared_task(): '
        'before=%d after=%d' % (len(recipes_before), len(recipes_after)))
"""
        with self._build_dir() as builddir:
            self._run_script(builddir, script)


class CookerTest(unittest.TestCase):
    def setUp(self):
        # At least one variable needs to be set
        self.d = bb.data.init()
        topdir = os.path.join(os.path.dirname(os.path.realpath(__file__)), "testdata/cooker")
        self.d.setVar('TOPDIR', topdir)

    def test_CookerCollectFiles_sublayers(self):
        '''Test that a sublayer of an existing layer does not trigger
           No bb files matched ...'''

        def append_collection(topdir, path, d):
            collection = path.split('/')[-1]
            pattern = "^" + topdir + "/" + path + "/"
            regex = re.compile(pattern)
            priority = 5

            d.setVar('BBFILE_COLLECTIONS', (d.getVar('BBFILE_COLLECTIONS') or "") + " " + collection)
            d.setVar('BBFILE_PATTERN_%s' % (collection), pattern)
            d.setVar('BBFILE_PRIORITY_%s' % (collection), priority)

            return (collection, pattern, regex, priority)

        topdir = self.d.getVar("TOPDIR")

        # Priorities: list of (collection, pattern, regex, priority)
        bbfile_config_priorities = []
        # Order is important for this test, shortest to longest is typical failure case
        bbfile_config_priorities.append( append_collection(topdir, 'first', self.d) )
        bbfile_config_priorities.append( append_collection(topdir, 'second', self.d) )
        bbfile_config_priorities.append( append_collection(topdir, 'second/third', self.d) )

        pkgfns = [ topdir + '/first/recipes/sample1_1.0.bb',
                   topdir + '/second/recipes/sample2_1.0.bb',
                   topdir + '/second/third/recipes/sample3_1.0.bb' ]

        class LogHandler(logging.Handler):
            def __init__(self):
                logging.Handler.__init__(self)
                self.logdata = []

            def emit(self, record):
                self.logdata.append(record.getMessage())

        # Move cooker to use my special logging
        logger = bb.cooker.logger
        log_handler = LogHandler()
        logger.addHandler(log_handler)
        collection = bb.cooker.CookerCollectFiles(bbfile_config_priorities)
        collection.collection_priorities(pkgfns, pkgfns, self.d)
        logger.removeHandler(log_handler)

        # Should be empty (no generated messages)
        expected = []

        self.assertEqual(log_handler.logdata, expected)


class BuildFileTest(_BitbakeSubprocessTestCase):
    """Tests for the buildfile ("bitbake -b") mode."""

    # parse-tests BBPATH: minimal bitbake.conf whose BBFILES honours
    # EXTRA_BBFILES and already includes *.bbappend
    _parsetests = os.path.realpath(os.path.join(os.path.dirname(__file__), "parse-tests"))

    recipe = """\
MARKER ??= "no-bbappend"
python do_marker() {
    with open(d.expand("${TOPDIR}/marker.log"), "w") as f:
        f.write(d.getVar("MARKER"))
}
addtask marker
"""

    bbappend = 'MARKER = "bbappend-applied"\n'

    def test_buildfile_applies_bbappends(self):
        """bitbake -b must build the recipe with its bbappends applied.

        buildFileInternal() looks the appends up in self.collections[mc], which
        on the -b path is only ever populated by matchFiles().
        """
        with tempfile.TemporaryDirectory(prefix="buildfilerecipes") as recipes, \
             self._build_dir(prefix="buildfiletest") as builddir:
            recipe = os.path.join(recipes, "appendtest.bb")
            with open(recipe, "w") as f:
                f.write(self.recipe)
            with open(os.path.join(recipes, "appendtest.bbappend"), "w") as f:
                f.write(self.bbappend)

            env = os.environ.copy()
            env["BBPATH"] = self._parsetests
            env["BB_ENV_PASSTHROUGH_ADDITIONS"] = "TOPDIR EXTRA_BBFILES"
            env["TOPDIR"] = builddir
            env["EXTRA_BBFILES"] = "%s/*.bb %s/*.bbappend" % (recipes, recipes)

            cmd = ["bitbake", "-b", recipe, "-c", "marker"]
            self._run_subprocess(cmd, env, builddir)

            with open(os.path.join(builddir, "marker.log")) as f:
                self.assertEqual(f.read(), "bbappend-applied",
                                 "bitbake -b did not apply the recipe's bbappend")
