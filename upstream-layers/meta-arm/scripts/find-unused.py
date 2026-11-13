#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
# Copyright (c) 2026 Arm Limited
"""Report dangling patches, unreferenced BSP includes, and candidate obsolete BSP recipes.

Static analysis only; no BitBake environment required. Recipe/include results are
review-only. Patch deletion is opt-in and still requires care with dynamic SRC_URI.
"""
import argparse
import fnmatch
import re
import subprocess
import sys
from collections import defaultdict
from pathlib import Path

META_EXT = {'.bb', '.bbappend', '.inc', '.conf', '.bbclass'}
ASSIGN = re.compile(r'^\s*([A-Za-z0-9_${}/.+%-]+(?::[A-Za-z0-9_${}/.+%-]+)*)\s*(?:\?\?=|\?=|:=|\+=|=\+|\.=|=\.|=)\s*(.*)$')
RECIPE = re.compile(r'^(.+)_([^_]+)\.(bb|bbappend)$')
TOKEN = re.compile(r'[A-Za-z0-9][A-Za-z0-9_.+%-]*')


def git(cmd, root):
    return subprocess.run(['git', '-C', str(root), *cmd], capture_output=True)


def load(root):
    r = git(['rev-parse', '--show-toplevel'], root)
    if r.returncode:
        raise RuntimeError('Not in a Git checkout; specify --root')
    root = Path(r.stdout.decode().strip()).resolve()
    r = git(['ls-files', '-z'], root)
    if r.returncode:
        raise RuntimeError('git ls-files failed')
    paths = [Path(p.decode('utf-8', 'surrogateescape')) for p in r.stdout.split(b'\0') if p]
    sources = {}
    for path in paths:
        if path.suffix in META_EXT:
            try:
                sources[path] = (root / path).read_text(encoding='utf-8', errors='replace')
            except OSError:
                pass
    return root, paths, sources


def in_bsp(path):
    return path.parts and path.parts[0] == 'meta-arm-bsp'


def recipe_tree(path):
    return any(part.startswith('recipes-') for part in path.parts)


def refs(name, sources, skip=None):
    return [p for p, text in sources.items() if p != skip and name in text]


def patches(paths, sources):
    for path in paths:
        if path.suffix != '.patch' or not recipe_tree(path):
            continue
        found = refs(path.name, sources)
        if not found:
            yield ('patches', path, 'No literal filename reference in tracked metadata', True)


def includes(paths, sources):
    for path in paths:
        if not in_bsp(path) or path.suffix != '.inc':
            continue
        found = refs(path.name, sources, path)
        if found:
            continue
        # A basename may be constructed from variable expansions, e.g.
        # require ${BPN}.inc. Treat these as ambiguous rather than safe.
        parent = path.parent
        dynamic = [p for p, content in sources.items()
                   if p != path and (p.parent == parent or parent in p.parents)
                   and re.search(r'(?m)^\s*(?:require|include)\s+[^\n#]*\$\{', content)]
        note = 'No literal include reference found'
        if dynamic:
            note += '; dynamic include(s) in ' + ', '.join(str(p) for p in dynamic[:3])
        yield ('includes', path, note + ' (review only)', False)


def logical_lines(text):
    return re.sub(r'\\\s*\n\s*', ' ', text).splitlines()


def metadata(sources):
    selections = defaultdict(list)
    mentions = defaultdict(set)
    for path, content in sources.items():
        for line in logical_lines(content):
            line = line.split('#', 1)[0]
            m = ASSIGN.match(line)
            if not m:
                continue
            key, value = m.groups()
            if key.startswith('PREFERRED_VERSION_'):
                name = key[len('PREFERRED_VERSION_'):].split(':', 1)[0]
                versions = re.findall(r'["\']([^"\']+)["\']', value)
                if versions:
                    selections[name].extend((v.strip(), path) for v in versions)
                continue
            # Only dependency/provider/image variables imply usage. Merely
            # mentioning a recipe in an arbitrary string is not sufficient.
            base = key.split(':', 1)[0]
            if (base in {'IMAGE_INSTALL', 'CORE_IMAGE_EXTRA_INSTALL', 'PACKAGE_INSTALL',
                         'MACHINE_ESSENTIAL_EXTRA_RDEPENDS', 'MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS',
                         'MACHINE_EXTRA_RDEPENDS', 'MACHINE_EXTRA_RRECOMMENDS',
                         'DEPENDS', 'RDEPENDS', 'RRECOMMENDS', 'EXTRA_IMAGEDEPENDS',
                         'PREFERRED_PROVIDER_virtual/kernel', 'PREFERRED_PROVIDER_virtual/bootloader'}
                    or base.startswith(('RDEPENDS_', 'RRECOMMENDS_', 'PREFERRED_PROVIDER_'))):
                for token in TOKEN.findall(value):
                    mentions[token].add(path)
    return selections, mentions


def version_matches(version, pattern):
    if '${' in pattern or '$(' in pattern:
        return None
    return fnmatch.fnmatchcase(version, pattern.replace('%', '*'))


def recipes(paths, sources, verbose=False):
    selections, mentions = metadata(sources)
    groups = defaultdict(list)
    for path in paths:
        if not in_bsp(path) or not recipe_tree(path):
            continue
        m = RECIPE.match(path.name)
        if m:
            name, version, kind = m.groups()
            groups[name].append((path, version, kind))

    for name, entries in sorted(groups.items()):
        picks = selections.get(name, [])
        usage = mentions.get(name, set())
        # Version-specific recipes are candidates only when there is evidence
        # of a version transition, or a vanished explicit pin. No pin and no
        # references is suspicious, but not proof: default selection is valid.
        for path, version, kind in entries:
            if version == '%' or '${' in version:
                continue
            matched = [(p, src) for p, src in picks if version_matches(version, p)]
            if matched:
                if verbose:
                    yield ('referenced', path, 'Version selected by ' + ', '.join(str(src) for _, src in matched[:4]), False)
                continue
            unknown = [(p, src) for p, src in picks if version_matches(version, p) is None]
            if unknown:
                if verbose:
                    yield ('referenced', path, 'Dynamic PREFERRED_VERSION: cannot resolve statically', False)
                continue
            # Check versions of the same type, not unrelated bb vs bbappend.
            peers = [(other, v) for other, v, k in entries if k == kind and other != path]
            newer_or_selected = [v for _, v in peers if v != '%' and (not picks or any(version_matches(v, p) for p, _ in picks))]
            if picks:
                reason = 'No matching PREFERRED_VERSION; selected: ' + ', '.join(sorted({p for p, _ in picks}))
                if not newer_or_selected:
                    reason += '; no alternative matching local version'
            else:
                reason = 'No PREFERRED_VERSION found'
                if peers:
                    reason += '; other local versions: ' + ', '.join(sorted({v for _, v in peers}))
            if usage:
                reason += '; recipe/provider referenced in ' + ', '.join(str(p) for p in sorted(usage)[:3])
                category = 'potentially obsolete'
            elif picks or peers:
                category = 'potentially obsolete'
                reason += '; no direct image/dependency/provider reference found'
            else:
                category = 'likely unused'
                reason += '; no direct image/dependency/provider reference found (default BitBake selection still possible)'
            yield ('recipes', path, f'{category}: {reason}', False)


def main():
    ap = argparse.ArgumentParser(
        description='Find cleanup candidates in a meta-arm Git checkout (no BitBake required).',
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""Checks and scope:
  patches   All layers: tracked .patch files with no literal filename reference
            in tracked BitBake metadata. Dynamic SRC_URI can cause false positives.
  includes  meta-arm-bsp only: .inc files without literal references; dynamic
            include/require statements are noted where detected.
  recipes   meta-arm-bsp only: versioned .bb/.bbappend files compared with
            PREFERRED_VERSION settings and image/dependency/provider mentions.
            Results are grouped by confidence: likely unused, then potentially
            obsolete. A recipe may be selected implicitly without a version pin.
  all       Run all checks (default).

Examples:
  scripts/find-unused.py                       # Report everything
  scripts/find-unused.py recipes --verbose     # Explain BSP recipe findings
  scripts/find-unused.py patches               # Find dangling patches
  scripts/find-unused.py includes              # Review BSP include files
  scripts/find-unused.py patches --delete      # Confirm each patch removal
  scripts/find-unused.py patches --delete --force
  scripts/find-unused.py --root /path/to/meta-arm all

Safety:
  Reporting is the default. Only dangling patches can be deleted; recipe
  and include findings are ALWAYS review-only, even with --delete --force.
  Searches use tracked files from Git but read the current working tree, so
  uncommitted edits to machine configurations affect the results. No BitBake
  parsing, overrides, providers, or dynamic variable expansions are resolved.
""")
    ap.add_argument('check', nargs='?', choices=['all', 'patches', 'includes', 'recipes'], default='all', help='Check to run (default: all)')
    ap.add_argument('--root', type=Path, default=Path.cwd(), help='Path inside the meta-arm Git checkout (default: current directory)')
    ap.add_argument('--verbose', action='store_true', help='Show reasons and selected-version matches, including referenced recipes')
    ap.add_argument('--delete', action='store_true', help='Prompt before deleting dangling patches only')
    ap.add_argument('--force', action='store_true', help='With --delete, skip prompts')
    ap.add_argument('--dry-run', action='store_true', help='Explicit report-only mode (the default)')
    args = ap.parse_args()
    if args.force and not args.delete:
        ap.error('--force requires --delete')
    if args.delete and args.dry_run:
        ap.error('--delete conflicts with --dry-run')
    try:
        root, paths, sources = load(args.root)
    except RuntimeError as exc:
        ap.error(str(exc))
    if args.verbose:
        print(f'Repository: {root} | tracked files: {len(paths)} | metadata: {len(sources)}')
    checks = {'patches': patches, 'includes': includes, 'recipes': recipes}
    total = 0
    for check, func in checks.items():
        if args.check not in ('all', check):
            continue
        found = list(func(paths, sources, args.verbose) if check == 'recipes' else func(paths, sources))
        visible = [f for f in found if f[0] != 'referenced']
        total += len(visible)
        print(f'\n{check.capitalize()} ({len(visible)} candidates):')
        if check == 'recipes':
            def recipe_rank(item):
                category, path, reason, _ = item
                if category == 'referenced':
                    return (2, str(path))
                if reason.startswith('likely unused:'):
                    return (0, str(path))
                return (1, str(path))
            found.sort(key=recipe_rank)
        last_level = None
        for category, path, reason, safe in found:
            if category == 'referenced' and not args.verbose:
                continue
            if check == 'recipes':
                level = ('Referenced' if category == 'referenced' else
                         'Likely unused' if reason.startswith('likely unused:') else
                         'Potentially obsolete')
                if level != last_level:
                    print(f'  {level}:')
                    last_level = level
                print(f'    {path}')
            else:
                print(f'  {path}')
            if args.verbose or check == 'recipes':
                print(f'      {reason}' if check == 'recipes' else f'    {reason}')
            if args.delete and safe:
                try:
                    yes = args.force or input('    Delete patch? [y/N] ').strip().lower() == 'y'
                except EOFError:
                    yes = False
                if yes:
                    try:
                        (root / path).unlink()
                        print('    Deleted')
                    except OSError as exc:
                        print(f'    ERROR: {exc}', file=sys.stderr)
            elif args.delete and not safe and category != 'referenced':
                print('    Review only; not deleted')
    print(f'\nTotal: {total} candidates. Static analysis; verify before removing files.')
    return 0


if __name__ == '__main__':
    sys.exit(main())
