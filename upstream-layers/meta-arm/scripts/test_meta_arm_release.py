#!/usr/bin/env python3
# SPDX-FileCopyrightText: 2026 Arm Limited
# SPDX-License-Identifier: MIT
"""Offline unit and integration tests for meta-arm-release.py."""

import importlib.util
import os
import subprocess
import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch

SCRIPT = Path(__file__).with_name('meta-arm-release.py')
spec = importlib.util.spec_from_file_location('meta_arm_release', SCRIPT)
release = importlib.util.module_from_spec(spec)
import sys
sys.modules[spec.name] = release
spec.loader.exec_module(release)


class VersionTests(unittest.TestCase):
    def test_initial_release_sequence(self):
        self.assertEqual(release.previous_series('6.0'), '5.3')
        self.assertEqual(release.previous_series('6.1'), '6.0')
        self.assertEqual(release.series_from_number(release.series_number('5.3') + 1), '6.0')

    def test_patch_version(self):
        self.assertEqual(release.next_patch('5.0'), '5.0.1')
        self.assertEqual(release.next_patch('5.0.4'), '5.0.5')

    def test_bad_version(self):
        with self.assertRaises(release.ReleaseError):
            release.series_number('6.4')


class GitFixture(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.repo = Path(self.temp.name)
        self.previous = Path.cwd()
        os.chdir(self.repo)
        self.addCleanup(os.chdir, self.previous)
        self.git('init', '-q', '-b', 'master')
        self.git('config', 'user.name', 'Test Maintainer')
        self.git('config', 'user.email', 'test@example.com')
        self.commit('initial')
        self.remote = self.repo / 'upstream.git'
        subprocess.check_call(['git', 'init', '-q', '--bare', str(self.remote)])
        self.git('remote', 'add', 'yoctoproject', str(self.remote))
        self.git('update-ref', 'refs/remotes/yoctoproject/master', self.git('rev-parse', 'master'))

    def git(self, *args):
        return subprocess.check_output(['git', *args], text=True).strip()

    def commit(self, subject):
        with (self.repo / 'history.txt').open('a') as stream:
            stream.write(subject + '\n')
        self.git('add', 'history.txt')
        self.git('commit', '-q', '-m', subject)

    def branch(self, name):
        self.git('branch', name)
        self.git('update-ref', f'refs/remotes/yoctoproject/{name}', self.git('rev-parse', name))

    def refresh(self, name):
        self.git('update-ref', f'refs/remotes/yoctoproject/{name}', self.git('rev-parse', name))


class ReleaseIntegrationTests(GitFixture):
    def test_existing_tag_and_update(self):
        self.branch('scarthgap')
        self.git('checkout', '-q', 'scarthgap')
        self.git('tag', '-a', '5.0', '-m', 'initial')
        self.git('tag', '-a', 'yocto-5.0', '-m', 'initial')
        self.commit('fix one')
        self.commit('fix two')
        self.refresh('scarthgap')
        self.git('checkout', '-q', 'master')
        manager = release.ReleaseManager()
        state = manager.status('scarthgap')
        self.assertEqual((state.series, state.last_tag, state.commits, state.next_tag),
                         ('5.0', '5.0', 2, '5.0.1'))

    def test_no_updates(self):
        self.branch('scarthgap')
        self.git('tag', '-a', '5.0', '-m', 'initial')
        self.git('tag', '-a', 'yocto-5.0', '-m', 'initial')
        state = release.ReleaseManager().status('scarthgap')
        self.assertEqual(state.commits, 0)
        self.assertEqual(state.next_tag, '-')

    def test_announcement_initial_count(self):
        self.branch('wrynose')
        self.git('tag', '-a', '6.0', '-m', 'initial')
        self.git('tag', '-a', 'yocto-6.0', '-m', 'initial')
        self.commit('master change')
        self.branch('blacksail')
        self.git('tag', '-a', '6.1', '-m', 'initial')
        self.git('tag', '-a', 'yocto-6.1', '-m', 'initial')
        manager = release.ReleaseManager()
        subject, body = manager.announcement('blacksail', '6.1')
        self.assertEqual(subject, 'meta-arm blacksail release')
        self.assertIn('1 commits since the previous Yocto Project release, 6.0', body)
        self.assertIn('The meta-arm team', body)
        self.assertNotIn('Changes included', body)

    def test_update_announcement_lists_changes(self):
        self.branch('scarthgap')
        self.git('checkout', '-q', 'scarthgap')
        self.git('tag', '-a', '5.0', '-m', 'initial')
        self.git('tag', '-a', 'yocto-5.0', '-m', 'initial')
        self.commit('fix one')
        self.git('tag', '-a', '5.0.1', '-m', 'update')
        self.git('tag', '-a', 'yocto-5.0.1', '-m', 'update')
        self.refresh('scarthgap')
        manager = release.ReleaseManager()
        subject, body = manager.announcement('scarthgap', '5.0.1')
        self.assertIn('fix one', body)
        self.assertIn('1 commits', body)
        self.assertIn('5.0.1 and yocto-5.0.1 tags', body)

    def test_push_dry_run_does_not_modify_remote(self):
        self.branch('scarthgap')
        self.git('tag', '-a', '5.0', '-m', 'initial')
        self.git('tag', '-a', 'yocto-5.0', '-m', 'initial')
        manager = release.ReleaseManager(execute=False)
        with patch('builtins.print') as printer:
            manager.push(['scarthgap'])
        self.assertTrue(any('New tags: 2' in str(call) for call in printer.call_args_list))



class MailerTests(unittest.TestCase):
    def test_auto_prefers_sendmail(self):
        with patch.object(release.shutil, 'which', side_effect=lambda x: {'sendmail': '/usr/bin/sendmail', 'mutt': '/usr/bin/mutt'}.get(x)):
            self.assertEqual(release.ReleaseManager(mailer='auto').mail_backend(), '/usr/bin/sendmail')

    def test_force_mutt(self):
        with patch.object(release.shutil, 'which', side_effect=lambda x: {'sendmail': '/usr/bin/sendmail', 'mutt': '/usr/bin/mutt'}.get(x)):
            self.assertEqual(release.ReleaseManager(mailer='mutt').mail_backend(), '/usr/bin/mutt')

    def test_force_sendmail_unavailable_does_not_fallback(self):
        with patch.object(release.shutil, 'which', side_effect=lambda x: {'mutt': '/usr/bin/mutt'}.get(x)), patch.object(release.os, 'access', return_value=False):
            manager = release.ReleaseManager(mailer='sendmail')
            self.assertIsNone(manager.mail_backend())
            with self.assertRaisesRegex(release.ReleaseError, 'sendmail'):
                manager.send('subject', 'body')

    def test_cli_override_env(self):
        with patch.dict(os.environ, {'META_ARM_MAILER': 'mutt'}):
            self.assertEqual(release.parser().parse_args(['--mailer', 'sendmail', 'check']).mailer, 'sendmail')
            self.assertEqual(release.parser().parse_args(['check']).mailer, 'mutt')

    def test_invalid_mailer_rejected(self):
        with self.assertRaises(release.ReleaseError):
            release.ReleaseManager(mailer='invalid')

if __name__ == '__main__':
    unittest.main(verbosity=2)


class EditorTests(unittest.TestCase):
    def test_editor_inherits_terminal_streams(self):
        manager = release.ReleaseManager(edit=True)
        with patch.object(manager, 'editor_command', return_value='vim -c "set spell"'):
            with patch.object(release.subprocess, 'run') as invoke:
                result = manager.edit_message('Subject', 'Body')
        self.assertIsNone(result)
        invoke.assert_called_once()
        args, kwargs = invoke.call_args
        self.assertEqual(args[0][:3], ['sh', '-c', 'vim -c "set spell" "$1"'])
        self.assertTrue(kwargs['check'])
        self.assertNotIn('stdout', kwargs)
        self.assertNotIn('stderr', kwargs)
        self.assertNotIn('stdin', kwargs)

    def test_editor_failure_prevents_send(self):
        manager = release.ReleaseManager(edit=True)
        with patch.object(manager, 'editor_command', return_value='vim'):
            with patch.object(release.subprocess, 'run', side_effect=subprocess.CalledProcessError(1, 'vim')):
                with self.assertRaisesRegex(release.ReleaseError, 'Editor exited unsuccessfully'):
                    manager.edit_message('Subject', 'Body')


class PlatformReleaseTests(GitFixture):
    def test_platform_tag_name_and_date(self):
        self.assertEqual(release.platform_tag('corstone1000', '2026.10.01'),
                         'CORSTONE1000-2026.10.01')
        with self.assertRaises(release.ReleaseError):
            release.platform_tag('corstone1000', '2026.02.30')

    def test_platform_dry_run_does_not_create_tag(self):
        manager = release.ReleaseManager()
        tag = manager.platform('corstone1000', 'HEAD', '2026.10.01')
        self.assertEqual(tag, 'CORSTONE1000-2026.10.01')
        self.assertIsNone(manager.tag_object(tag))

    def test_platform_existing_tag_is_idempotent(self):
        self.git('tag', '-a', 'CORSTONE1000-2026.10.01', '-m', 'release')
        manager = release.ReleaseManager(execute=True)
        self.assertEqual(manager.platform('corstone1000', 'HEAD', '2026.10.01'),
                         'CORSTONE1000-2026.10.01')

    def test_push_explicit_platform_tag(self):
        self.git('tag', '-a', 'CORSTONE1000-2026.10.01', '-m', 'release')
        manager = release.ReleaseManager(execute=True)
        manager.push(('CORSTONE1000-2026.10.01',))
        remote = self.git('ls-remote', '--tags', '--refs', 'yoctoproject',
                          'refs/tags/CORSTONE1000-2026.10.01')
        self.assertIn('refs/tags/CORSTONE1000-2026.10.01', remote)

    def test_push_auto_discovers_platform_tag(self):
        self.git('tag', '-a', 'CORSTONE1000-2026.10.01', '-m', 'release')
        manager = release.ReleaseManager(execute=True)
        manager.push()
        remote = self.git('ls-remote', '--tags', '--refs', 'yoctoproject',
                          'refs/tags/CORSTONE1000-2026.10.01')
        self.assertIn('refs/tags/CORSTONE1000-2026.10.01', remote)
