#!/usr/bin/env python3
# SPDX-FileCopyrightText: 2026 Arm Limited
# SPDX-License-Identifier: MIT
"""Create, publish and announce meta-arm release tags."""

import argparse
from datetime import date, datetime
import os
import re
import shlex
import shutil
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from email.message import EmailMessage
from pathlib import Path

SUPPORT_URL = 'https://docs.yoctoproject.org/ref-manual/release-process.html'
VERSION_RE = re.compile(r'^(\d+)\.(\d+)(?:\.(\d+))?$')
PLATFORM_TAG_RE = re.compile(r'^[A-Z][A-Z0-9_-]*-[0-9]{4}\.[0-9]{2}\.[0-9]{2}$')
DEFAULT_LTS = ('scarthgap', 'wrynose')


class ReleaseError(Exception):
    pass


def run(*argv, check=True):
    result = subprocess.run(argv, text=True, stdout=subprocess.PIPE,
                            stderr=subprocess.PIPE)
    if check and result.returncode:
        raise ReleaseError(f"{' '.join(argv)}: {result.stderr.strip()}")
    return result


def git(*args, check=True):
    return run('git', *args, check=check)


def series_number(version):
    match = VERSION_RE.fullmatch(version)
    if not match or match.group(3) is not None or int(match.group(2)) > 3:
        raise ReleaseError(f'Invalid initial release version: {version}')
    return int(match.group(1)) * 4 + int(match.group(2))


def series_from_number(number):
    return f'{number // 4}.{number % 4}'


def previous_series(version):
    number = series_number(version)
    if number == 0:
        raise ReleaseError('No previous release version')
    return series_from_number(number - 1)


def next_patch(version):
    match = VERSION_RE.fullmatch(version)
    if not match:
        raise ReleaseError(f'Invalid version: {version}')
    return f'{match.group(1)}.{match.group(2)}.{int(match.group(3) or 0) + 1}'


def platform_tag(name, release_date=None):
    """Build a platform tag, validating both platform name and calendar date."""
    if not re.fullmatch(r'[A-Za-z][A-Za-z0-9_-]*', name):
        raise ReleaseError(f'Invalid platform name: {name}')
    if release_date is None:
        release_date = date.today().strftime('%Y.%m.%d')
    try:
        datetime.strptime(release_date, '%Y.%m.%d')
    except ValueError as exc:
        raise ReleaseError('--date must be a valid YYYY.MM.DD date') from exc
    if not re.fullmatch(r'\d{4}\.\d{2}\.\d{2}', release_date):
        raise ReleaseError('--date must use YYYY.MM.DD')
    return f'{name.upper()}-{release_date}'


@dataclass
class BranchStatus:
    branch: str
    series: str
    last_tag: str | None
    commits: int | None
    next_tag: str


class ReleaseManager:
    def __init__(self, remote='yoctoproject', recipient='meta-arm@lists.yoctoproject.org',
                 lts=DEFAULT_LTS, execute=False, edit=False, as_of=None, force_lts=False, mailer='auto'):
        self.remote = remote
        self.recipient = recipient
        self.lts = tuple(lts)
        self.execute = execute
        self.edit = edit
        self.as_of = as_of
        self.force_lts = force_lts
        if mailer not in ('auto', 'sendmail', 'mutt'):
            raise ReleaseError(f'Invalid mailer: {mailer}')
        self.mailer = mailer
        self._branches = None
        self._series = None

    def branch_ref(self, branch):
        return f'refs/remotes/{self.remote}/{branch}'

    def branches(self):
        if self._branches is None:
            refs = git('for-each-ref', '--format=%(refname:strip=3)',
                       f'refs/remotes/{self.remote}/').stdout.splitlines()
            self._branches = sorted(b for b in refs if b not in ('master', 'testing') and '/' not in b)
            if not self._branches:
                raise ReleaseError(f'No release branches under {self.remote}')
        return self._branches

    def require_branch(self, branch):
        if branch not in self.branches():
            raise ReleaseError(f'Unknown or excluded upstream branch: {branch}')

    @staticmethod
    def ancestor(older, newer):
        return git('merge-base', '--is-ancestor', older, newer, check=False).returncode == 0

    @staticmethod
    def tag_commit(tag):
        result = git('rev-parse', '--verify', '-q', f'refs/tags/{tag}^{{commit}}', check=False)
        return result.stdout.strip() if result.returncode == 0 else None

    @staticmethod
    def tag_object(tag):
        result = git('rev-parse', '--verify', '-q', f'refs/tags/{tag}', check=False)
        return result.stdout.strip() if result.returncode == 0 else None

    def numeric_tags(self):
        return [tag for tag in git('tag', '-l').stdout.splitlines() if VERSION_RE.fullmatch(tag)]

    def discover_series(self):
        """Use tags that lie on a branch, not inherited tags on its fork history.

        A branch's initial release can be at the fork itself; in that case,
        resolve shared tags by proximity and fail closed if ambiguous.
        """
        if self._series is not None:
            return self._series
        branches = self.branches()
        tags = self.numeric_tags()
        forks = {}
        for branch in branches:
            result = git('merge-base', self.branch_ref(branch), self.branch_ref('master'))
            forks[branch] = result.stdout.strip()

        # For every series, assign it to the branch whose tip is nearest
        # to its latest reachable tag. Ties cannot establish ownership.
        candidates = {}
        for tag in tags:
            match = VERSION_RE.fullmatch(tag)
            series = f'{match.group(1)}.{match.group(2)}'
            for branch in branches:
                if not self.ancestor(f'refs/tags/{tag}', self.branch_ref(branch)):
                    continue
                distance = int(git('rev-list', '--count',
                                   f'{tag}..{self.branch_ref(branch)}').stdout)
                current = candidates.setdefault(series, {})
                if branch not in current or distance < current[branch][0] or (
                        distance == current[branch][0] and
                        (int(match.group(3) or 0) > current[branch][1])):
                    current[branch] = (distance, int(match.group(3) or 0), tag)

        owners = {}
        for series, choices in candidates.items():
            ranked = sorted((v[0], -v[1], b) for b, v in choices.items())
            if not ranked:
                continue
            # If multiple branches are tied, avoid guessing ownership.
            if len(ranked) > 1 and ranked[0][:2] == ranked[1][:2]:
                raise ReleaseError(f'Ambiguous ownership for {series}: {ranked[0][2]}, {ranked[1][2]}')
            owners[series] = ranked[0][2]

        by_branch = {}
        for series, branch in owners.items():
            if branch in by_branch:
                raise ReleaseError(f'Multiple version series appear to belong to {branch}')
            by_branch[branch] = series

        if not by_branch:
            raise ReleaseError('No release version anchors found')

        # Infer only unreleased branches, after the highest established series.
        missing = [b for b in branches if b not in by_branch]
        if missing:
            latest = max(series_number(s) for s in by_branch.values())
            ordered = sorted(missing, key=lambda b: int(git('show', '-s', '--format=%ct', forks[b]).stdout))
            for earlier, later in zip(ordered, ordered[1:]):
                if not self.ancestor(forks[earlier], forks[later]) or forks[earlier] == forks[later]:
                    raise ReleaseError(f'Cannot order unreleased branches: {earlier}, {later}')
            for branch in ordered:
                latest += 1
                by_branch[branch] = series_from_number(latest)
        self._series = by_branch
        return by_branch

    def latest_tag(self, branch):
        series = self.discover_series()[branch]
        choices = []
        for tag in self.numeric_tags():
            match = VERSION_RE.fullmatch(tag)
            if f'{match.group(1)}.{match.group(2)}' != series:
                continue
            if self.ancestor(f'refs/tags/{tag}', self.branch_ref(branch)):
                choices.append((int(match.group(3) or 0), tag))
        return max(choices)[1] if choices else None

    def status(self, branch):
        self.require_branch(branch)
        series = self.discover_series()[branch]
        last = self.latest_tag(branch)
        if not last:
            return BranchStatus(branch, series, None, None, f'Initial {series}')
        count = int(git('rev-list', '--count', f'{last}..{self.branch_ref(branch)}').stdout)
        return BranchStatus(branch, series, last, count, next_patch(last) if count else '-')

    def show_status(self, branches):
        print(f'{"Branch":<18} {"Version":<9} {"Last-tag":<14} {"Commits":<10} Next-tag')
        for branch in branches:
            state = self.status(branch)
            print(f'{branch:<18} {state.series:<9} {state.last_tag or "(none)":<14} '
                  f'{str(state.commits) if state.commits is not None else "-":<10} {state.next_tag}')

    def create_tags(self, branch, version, ref, kind):
        self.require_branch(branch)
        if not VERSION_RE.fullmatch(version):
            raise ReleaseError(f'Invalid tag version: {version}')
        target = git('rev-parse', '--verify', f'{ref}^{{commit}}').stdout.strip()
        if not self.ancestor(target, self.branch_ref(branch)):
            raise ReleaseError(f'{ref} is not on {branch}')
        for tag in (version, f'yocto-{version}'):
            old = self.tag_commit(tag)
            if old and old != target:
                raise ReleaseError(f'{tag} already points to another commit')
        print(f'{branch}: {version} -> {target[:12]} ({kind})')
        for tag in (version, f'yocto-{version}'):
            if self.tag_object(tag):
                print(f'  Exists: {tag}')
            elif self.execute:
                git('tag', '-s', tag, '-m', f'{kind} for Yocto Project {version} ({branch})', target)
                print(f'  Created: {tag}')
            else:
                print(f'  DRY RUN: create {tag}')
        self._series = None

    def release(self, branch, version):
        self.require_branch(branch)
        expected = self.discover_series()[branch]
        if version != expected:
            raise ReleaseError(f'Expected {expected} for {branch}, got {version}')
        ref = self.branch_ref(branch)
        if self.as_of:
            ref = git('rev-list', '--first-parent', '--before', f'{self.as_of} 23:59:59',
                      '-1', ref).stdout.strip()
            if not ref:
                raise ReleaseError(f'No commit on {branch} before {self.as_of}')
        self.create_tags(branch, version, ref, 'Initial release')

    def update(self, branches):
        created = []
        for branch in branches:
            state = self.status(branch)
            if not state.last_tag:
                print(f'{branch}: initial release {state.series} missing; skipping')
            elif not state.commits:
                print(f'{branch}: no new commits; skipping')
            else:
                self.create_tags(branch, state.next_tag, self.branch_ref(branch), 'Update')
                created.append((branch, state.next_tag))
        return created

    def platform(self, name, ref, release_date=None):
        """Create a signed platform tag without requiring release branch discovery."""
        tag = platform_tag(name, release_date)
        target = git('rev-parse', '--verify', f'{ref}^{{commit}}').stdout.strip()
        existing = self.tag_object(tag)
        if existing:
            if self.tag_commit(tag) != target:
                raise ReleaseError(f'{tag} already exists at another commit')
            print(f'Platform tag already exists: {tag} -> {target[:12]}')
            return tag
        print(f'Platform: {name}\nCommit:   {target[:12]}\nTag:      {tag}')
        if self.execute:
            message = f'{name.upper()} platform release ({tag.rsplit("-", 1)[1]})\n\nRelease of the {name} platform at commit {target}.'
            git('tag', '-s', tag, '-m', message, target)
            print(f'Created signed tag: {tag}')
        else:
            print(f'DRY RUN: Would create signed tag {tag}.')
        return tag

    def tags_for(self, branches):
        series_map = self.discover_series()
        selected = branches or self.branches()
        result = set()
        for branch in selected:
            self.require_branch(branch)
            series = series_map[branch]
            for tag in self.numeric_tags():
                match = VERSION_RE.fullmatch(tag)
                if f'{match.group(1)}.{match.group(2)}' != series:
                    continue
                if not self.ancestor(f'refs/tags/{tag}', self.branch_ref(branch)):
                    continue
                paired = f'yocto-{tag}'
                if not self.tag_commit(paired) or self.tag_commit(paired) != self.tag_commit(tag):
                    raise ReleaseError(f'Missing or mismatched paired tag: {paired}')
                result.update((tag, paired))
        return sorted(result)

    def push(self, selections=(), all_tags=False):
        """Push selected tags, or all recognized release tags, without overwrites.

        For compatibility, a selection may be an existing release branch.
        Explicit tag names need not match the recognized tag patterns.
        """
        if all_tags:
            tags = git('tag', '-l').stdout.splitlines()
        elif selections:
            tags = set()
            for selection in selections:
                if self.tag_object(selection):
                    tags.add(selection)
                elif selection in self.branches():
                    tags.update(self.tags_for((selection,)))
                else:
                    raise ReleaseError(f'Unknown tag or release branch: {selection}')
            tags = sorted(tags)
        else:
            tags = [t for t in git('tag', '-l').stdout.splitlines()
                    if VERSION_RE.fullmatch(t)
                    or (t.startswith('yocto-') and VERSION_RE.fullmatch(t[6:]))
                    or PLATFORM_TAG_RE.fullmatch(t)]
        if not tags:
            print('No matching release tags')
            return
        remote = {}
        for line in git('ls-remote', '--tags', '--refs', self.remote).stdout.splitlines():
            sha, ref = line.split()
            remote[ref.removeprefix('refs/tags/')] = sha
        pending = []
        published = 0
        for tag in tags:
            local = self.tag_object(tag)
            old = remote.get(tag)
            if old is None:
                pending.append(tag)
            elif old == local:
                published += 1
            else:
                raise ReleaseError(f'Tag conflict on {self.remote}: {tag} local={local} remote={old}')
        print(f'Upstream: {self.remote}\nAlready published: {published}\nNew tags: {len(pending)}')
        for tag in pending:
            print(f'  {tag}')
        if pending and self.execute:
            git('push', '--atomic', self.remote,
                *(f'refs/tags/{tag}:refs/tags/{tag}' for tag in pending))
            print(f'Published {len(pending)} tags.')
        elif not self.execute:
            print('DRY RUN: No tags pushed.')

    def lts_description(self, branch):
        if branch in self.lts or self.force_lts:
            opening = ('This is a Long Term Support (LTS) release. meta-arm generally aims '
                       'to follow the Yocto Project\'s LTS support lifecycle on a best-effort '
                       'basis, with maintenance primarily focused on bug fixes and security '
                       'fixes. CI testing is performed on supported configurations as resources permit.')
        else:
            opening = ('This is NOT a Long Term Support (LTS) release. meta-arm generally aims '
                       'to follow the Yocto Project\'s release support lifecycle on a best-effort '
                       'basis. For non-LTS releases, this means approximately seven months of '
                       'limited maintenance, primarily consisting of bug fixes and security fixes, '
                       'with minimal CI testing.')
        if self.lts:
            names = ', '.join(self.lts[:-1]) + (' and ' if len(self.lts) > 1 else '') + self.lts[-1]
            opening += f'\n\nThe currently supported LTS branches are {names}.'
        return opening + ('\n\nFor more information about the Yocto Project\'s release '
                          f'lifecycle and LTS policy, see:\n{SUPPORT_URL}')

    def announcement(self, branch, version):
        self.require_branch(branch)
        series = self.discover_series()[branch]
        if version != series and not re.fullmatch(re.escape(series) + r'\.\d+', version):
            raise ReleaseError(f'{version} does not belong to {branch} ({series})')
        if not self.tag_commit(version) or self.tag_commit(version) != self.tag_commit(f'yocto-{version}'):
            raise ReleaseError(f'Missing or mismatched release tag pair: {version}')
        if not self.ancestor(f'refs/tags/{version}', self.branch_ref(branch)):
            raise ReleaseError(f'{version} is not on {branch}')
        lts_text = self.lts_description(branch)
        if version == series:
            previous = previous_series(version)
            if not self.tag_commit(previous):
                raise ReleaseError(f'Missing previous initial release tag {previous}')
            count = git('rev-list', '--count', f'{previous}..{version}').stdout.strip()
            subject = f'meta-arm {branch} release'
            body = (f'meta-arm has released the {branch} support branch, tagged as {version}.\n\n'
                    f'{lts_text}\n\nThis release contains {count} commits since the previous '
                    f'Yocto Project release, {previous}.')
        else:
            patch = int(version.rsplit('.', 1)[1])
            previous = series if patch == 1 else f'{series}.{patch - 1}'
            if not self.tag_commit(previous) or not self.ancestor(previous, version):
                raise ReleaseError(f'Missing or non-ancestor previous tag: {previous}')
            log = git('log', '--reverse', '--format=%h %s', f'{previous}..{version}').stdout.strip()
            count = int(git('rev-list', '--count', f'{previous}..{version}').stdout)
            if not count:
                raise ReleaseError('No commits in update release')
            dates = git('log', '--reverse', '--format=%cs', f'{previous}..{version}').stdout.splitlines()
            first, last = dates[0], git('log', '-1', '--format=%cs', version).stdout.strip()
            subject = f'meta-arm {branch} update'
            body = (f'meta-arm has released an updated {branch} support tag, {version}.\n\n'
                    f'{lts_text}\n\nThis release contains {count} commits between {first} and {last}.\n\n'
                    'Changes included in this release:\n\n' +
                    '\n'.join('  ' + line for line in log.splitlines()))
        body += (f'\n\nThe release is available under the {version} and yocto-{version} tags.\n'
                 'Both tags are GPG signed.\n\nThanks,\nThe meta-arm team')
        return subject, body

    @staticmethod
    def editor_command():
        # git var GIT_EDITOR already respects GIT_EDITOR, VISUAL, EDITOR and core.editor.
        result = git('var', 'GIT_EDITOR', check=False)
        return result.stdout.strip() if result.returncode == 0 and result.stdout.strip() else 'vi'

    def edit_message(self, subject, body):
        original = f'Subject: {subject}\n\n{body}\n'
        with tempfile.TemporaryDirectory(prefix='meta-arm-release-') as directory:
            path = Path(directory) / 'announcement.txt'
            path.write_text(original, encoding='utf-8')
            editor = self.editor_command()
            print(f'Opening announcement in {editor}...', flush=True)
            # Interactive editors must inherit stdin, stdout, and stderr.
            # Git editor settings may contain shell quoting and arguments.
            try:
                subprocess.run(
                    ['sh', '-c', f'{editor} "$1"', 'editor', str(path)],
                    check=True,
                )
            except subprocess.CalledProcessError as exc:
                raise ReleaseError('Editor exited unsuccessfully; email not sent') from exc
            edited = path.read_text(encoding='utf-8')
        if edited == original:
            print('Email unchanged; not sending.')
            return None
        header, separator, text = edited.partition('\n\n')
        if not separator or not header.startswith('Subject: '):
            raise ReleaseError('Edited email must begin with Subject: followed by a blank line')
        new_subject = header.removeprefix('Subject: ').strip()
        if not new_subject or '\r' in new_subject or '\n' in new_subject or not text.strip():
            raise ReleaseError('Invalid edited email subject or body')
        return new_subject, text.rstrip('\n')

    def mail_backend(self):
        """Resolve the requested mailer without silently changing forced choices."""
        sendmail = shutil.which('sendmail')
        if not sendmail and os.access('/usr/sbin/sendmail', os.X_OK):
            sendmail = '/usr/sbin/sendmail'
        mutt = shutil.which('mutt')

        if self.mailer == 'sendmail':
            return sendmail
        if self.mailer == 'mutt':
            return mutt
        return sendmail or mutt

    def send(self, subject, body):
        backend = self.mail_backend()
        if not backend:
            raise ReleaseError(f'Requested mailer {self.mailer!r} is unavailable' if self.mailer != 'auto'
                               else 'Neither sendmail nor mutt is available')
        if os.path.basename(backend) == 'mutt':
            subprocess.run([backend, '-s', subject, '--', self.recipient],
                           input=body + '\n', text=True, check=True)
        else:
            message = EmailMessage()
            message['To'] = self.recipient
            message['Subject'] = subject
            message.set_content(body)
            subprocess.run([backend, '-t', '-i'], input=message.as_bytes(), check=True)

    def email(self, branch, version):
        subject, body = self.announcement(branch, version)
        if self.edit:
            changed = self.edit_message(subject, body)
            if changed is None:
                return
            subject, body = changed
        print(f'\nSubject: {subject}\n\n{body}\n')
        if not self.execute:
            print('DRY RUN: Email not sent.')
            return
        for tag in (version, f'yocto-{version}'):
            output = git('ls-remote', '--tags', '--refs', self.remote, f'refs/tags/{tag}').stdout
            entries = dict(line.split()[::-1] for line in output.splitlines())
            if entries.get(f'refs/tags/{tag}') != self.tag_object(tag):
                raise ReleaseError(f'{tag} is not published identically on {self.remote}')
        self.send(subject, body)
        print('Email submitted successfully.')

    def check(self):
        print('meta-arm release environment\n' + '-' * 48)
        errors = 0
        inside = git('rev-parse', '--is-inside-work-tree', check=False).returncode == 0
        print(f'Git repository:       {"OK" if inside else "FAILED"}')
        errors += not inside
        url = git('remote', 'get-url', self.remote, check=False)
        print(f'Upstream remote:      {self.remote} ({url.stdout.strip() or "missing"})')
        reachable = url.returncode == 0 and git('ls-remote', '--heads', self.remote, check=False).returncode == 0
        print(f'Upstream reachable:   {"OK" if reachable else "FAILED"}')
        errors += not reachable
        master = git('rev-parse', '--verify', '-q', f'{self.branch_ref("master")}^{{commit}}', check=False).returncode == 0
        print(f'Master branch:        {"OK" if master else "MISSING"}')
        errors += not master
        if master:
            print(f'Release branches:     {len(self.branches())}')
        identity = (git('config', 'user.name', check=False).stdout.strip(),
                    git('config', 'user.email', check=False).stdout.strip())
        print(f'Git identity:         {identity[0]} <{identity[1]}>' if all(identity)
              else 'Git identity:         NOT CONFIGURED')
        errors += not all(identity)
        signing = git('config', 'user.signingkey', check=False).stdout.strip()
        print(f'GPG signing key:      {signing or "Git default (not verified)"}')
        mailer_path = self.mail_backend()
        mailer_note = ' (forced)' if self.mailer != 'auto' else ' (auto)'
        print(f'Email delivery:       {(mailer_path or "Not available") + mailer_note}')
        print(f'Email recipient:      {self.recipient}')
        print(f'Editor:               {self.editor_command()}')
        print(f'LTS branches:         {", ".join(self.lts)}')
        print('Environment ready.' if not errors else f'Environment check failed: {errors} error(s).')
        return not errors


class FriendlyArgumentParser(argparse.ArgumentParser):
    """Display the curated usage page without argparse's duplicate sections."""

    def format_help(self):
        return self.description + '\n'


def parser():
    """Build a CLI with a compact, example-driven help page."""
    help_text = """\
Usage: meta-arm-release.py COMMAND [ARGUMENTS] [OPTIONS]

Commands:
  check                       Check the release environment
  status [BRANCH...]          Show release status and pending commits
  release BRANCH VERSION      Create initial signed release tags (x.y)
  update [BRANCH...]          Tag branches containing new commits (x.y.z)
  tag BRANCH VERSION REF      Tag a specific historical commit
  platform NAME REF           Create a signed platform release tag
  push [TAG|BRANCH...]        Publish missing release tags to upstream
  email BRANCH VERSION        Preview or send an announcement
  all BRANCH VERSION          Initial release, updates, push and announcements

Options:
  -h, --help, -?              Display this help
  -n, --dry-run               Preview operations (default)
  -x, --execute               Perform requested operations
  -e, --edit                  Edit announcements using your configured editor
  --lts                       Treat specified release as LTS
  --as-of YYYY-MM-DD          Select historical first-parent commit by date
  --date YYYY.MM.DD           Platform tag date (default: today)
  --remote NAME               Upstream remote (default: yoctoproject)
  --email-to ADDRESS          Override announcement recipient
  --mailer {auto,sendmail,mutt}  Select email delivery method (default: auto)
  --lts-branches "A B"        Set supported LTS branches

Examples:
  meta-arm-release.py check
  meta-arm-release.py status
  meta-arm-release.py status scarthgap wrynose
  meta-arm-release.py release blacksail 6.1
  meta-arm-release.py release blacksail 6.1 --execute
  meta-arm-release.py update scarthgap whinlatter --execute
  meta-arm-release.py tag wrynose 6.0 COMMIT --execute
  meta-arm-release.py platform corstone1000 HEAD
  meta-arm-release.py platform corstone1000 HEAD --date 2026.10.01 --execute
  meta-arm-release.py push CORSTONE1000-2026.10.01 --execute
  meta-arm-release.py push
  meta-arm-release.py push --execute
  meta-arm-release.py email blacksail 6.1
  meta-arm-release.py email blacksail 6.1 --edit
  meta-arm-release.py email blacksail 6.1 --edit --execute
  meta-arm-release.py email blacksail 6.1 --mailer mutt --execute
  meta-arm-release.py all blacksail 6.1 --execute

Notes:
  * Dry-run is the default; --execute does not ask for confirmation.
  * Email previews require no mail program; automatic delivery prefers
    sendmail and falls back to mutt. Use --mailer to force a backend.
  * Editor selection follows Git configuration (including VISUAL/EDITOR).
  * Release branches must exist upstream; branches containing '/' are skipped.
  * Existing tags are never overwritten; only missing tags are pushed.
  * Push without arguments discovers Yocto and platform release tags.
  * Push operations use only the configured upstream remote.
  * The script never checks out branches.

Environment variables:
  META_ARM_RELEASE_REMOTE      Override upstream remote
  META_ARM_RELEASE_EMAIL       Override announcement recipient
  META_ARM_LTS_BRANCHES        Space-separated LTS branches
  META_ARM_MAILER              auto, sendmail, or mutt
"""
    p = FriendlyArgumentParser(
        prog='meta-arm-release.py',
        description=help_text,
        formatter_class=argparse.RawDescriptionHelpFormatter,
        add_help=False,
    )
    p.add_argument('-h', '--help', '-?', action='help', help=argparse.SUPPRESS)
    p.add_argument('--execute', '-x', action='store_true', help=argparse.SUPPRESS)
    p.add_argument('--dry-run', '-n', action='store_true', help=argparse.SUPPRESS)
    p.add_argument('--edit', '-e', action='store_true', help=argparse.SUPPRESS)
    p.add_argument('--lts', action='store_true', help=argparse.SUPPRESS)
    p.add_argument('--as-of', metavar='YYYY-MM-DD', help=argparse.SUPPRESS)
    p.add_argument('--date', metavar='YYYY.MM.DD', help=argparse.SUPPRESS)
    p.add_argument('--remote', default=os.getenv('META_ARM_RELEASE_REMOTE', 'yoctoproject'),
                   help=argparse.SUPPRESS)
    p.add_argument('--email-to', default=os.getenv('META_ARM_RELEASE_EMAIL', 'meta-arm@lists.yoctoproject.org'),
                   help=argparse.SUPPRESS)
    p.add_argument('--mailer', choices=('auto', 'sendmail', 'mutt'),
                   default=os.getenv('META_ARM_MAILER', 'auto'), help=argparse.SUPPRESS)
    p.add_argument('--lts-branches', default=os.getenv('META_ARM_LTS_BRANCHES', 'scarthgap wrynose'),
                   help=argparse.SUPPRESS)
    sub = p.add_subparsers(dest='command', required=True, metavar='COMMAND')
    sub.add_parser('check', add_help=False)
    q = sub.add_parser('status', add_help=False)
    q.add_argument('branches', nargs='*')
    q = sub.add_parser('release', add_help=False)
    q.add_argument('branch')
    q.add_argument('version')
    q = sub.add_parser('update', add_help=False)
    q.add_argument('branches', nargs='*')
    q = sub.add_parser('tag', add_help=False)
    q.add_argument('branch')
    q.add_argument('version')
    q.add_argument('ref')
    q = sub.add_parser('platform', add_help=False)
    q.add_argument('name')
    q.add_argument('ref')
    q = sub.add_parser('push', add_help=False)
    q.add_argument('selections', nargs='*')
    q = sub.add_parser('email', add_help=False)
    q.add_argument('branch')
    q.add_argument('version')
    q = sub.add_parser('all', add_help=False)
    q.add_argument('branch')
    q.add_argument('version')
    return p


def main(argv=None):
    p = parser()
    # Support global options either before or after the subcommand.
    args = list(sys.argv[1:] if argv is None else argv)
    if not args or any(token in ('-h', '--help', '-?') for token in args):
        p.print_help()
        return 0
    globals_with_value = ('--as-of', '--date', '--remote', '--email-to', '--lts-branches', '--mailer')
    globals_flags = ('--execute', '-x', '--dry-run', '-n', '--edit', '-e', '--lts')
    front, rest = [], []
    i = 0
    while i < len(args):
        token = args[i]
        if token in globals_with_value:
            if i + 1 >= len(args):
                p.error(f'{token} requires a value')
            front.extend(args[i:i+2]); i += 2
        elif token in globals_flags:
            front.append(token); i += 1
        else:
            rest.append(token); i += 1
    ns = p.parse_args(front + rest)
    if ns.email_to.find('\n') >= 0 or ns.email_to.find('\r') >= 0:
        p.error('Invalid email recipient')
    if ns.date and ns.command != 'platform':
        p.error('--date only applies to platform')
    if ns.edit and ns.command not in ('email', 'all'):
        p.error('--edit only applies to email and all')
    if ns.as_of and not re.fullmatch(r'\d{4}-\d{2}-\d{2}', ns.as_of):
        p.error('--as-of requires YYYY-MM-DD')
    manager = ReleaseManager(ns.remote, ns.email_to, ns.lts_branches.split(),
                             ns.execute and not ns.dry_run, ns.edit, ns.as_of, ns.lts, ns.mailer)
    if ns.command == 'check':
        return 0 if manager.check() else 1
    if git('rev-parse', '--is-inside-work-tree', check=False).returncode:
        raise ReleaseError('Not in a Git repository')
    if ns.command == 'status':
        manager.show_status(ns.branches or manager.branches())
    elif ns.command == 'release':
        manager.release(ns.branch, ns.version)
    elif ns.command == 'update':
        manager.update(ns.branches or manager.branches())
    elif ns.command == 'tag':
        manager.require_branch(ns.branch)
        expected = manager.discover_series()[ns.branch]
        if ns.version != expected and not re.fullmatch(re.escape(expected) + r'\.\d+', ns.version):
            raise ReleaseError(f'Wrong version series for {ns.branch}: {ns.version}')
        manager.create_tags(ns.branch, ns.version, ns.ref, 'Release')
    elif ns.command == 'platform':
        manager.platform(ns.name, ns.ref, ns.date)
    elif ns.command == 'push':
        manager.push(ns.selections)
    elif ns.command == 'email':
        manager.email(ns.branch, ns.version)
    elif ns.command == 'all':
        manager.release(ns.branch, ns.version)
        created = [(ns.branch, ns.version)]
        created += manager.update([b for b in manager.branches() if b != ns.branch])
        if manager.execute:
            manager.push()
            for branch, version in created:
                manager.email(branch, version)
        else:
            print('DRY RUN: No tags created, pushed or emails sent.')
    return 0


if __name__ == '__main__':
    try:
        sys.exit(main())
    except (ReleaseError, subprocess.CalledProcessError) as error:
        print(f'ERROR: {error}', file=sys.stderr)
        sys.exit(1)
