#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
# Copyright (c) 2026 Arm Limited
"""Report the age, change history, and upstream status of tracked patches."""

import argparse
import csv
import datetime as dt
import re
import subprocess
import sys
from collections import Counter, defaultdict
from pathlib import Path


def git(root, *args):
    result = subprocess.run(['git', '-C', str(root), *args], capture_output=True)
    if result.returncode:
        raise RuntimeError(result.stderr.decode('utf-8', 'replace').strip())
    return result.stdout


def metadata(root, path):
    raw = git(root, 'log', '--follow', '--format=%as', '--', path)
    dates = [x for x in raw.decode().splitlines() if x]
    if not dates:
        return '', '', 0
    return dates[-1], dates[0], len(dates)


def upstream_status(path):
    try:
        content = path.read_text(encoding='utf-8', errors='replace')
    except OSError as exc:
        print(f'warning: cannot read {path}: {exc}', file=sys.stderr)
        return 'Missing', ''
    match = re.search(r'^\s*Upstream-Status:\s*(.*?)\s*$', content, re.I | re.M)
    if not match:
        return 'Missing', ''
    full = match.group(1)
    token = re.match(r'([^\s\[:]+)', full)
    return (token.group(1) if token else 'Unknown'), full


def layer_of(path):
    return next((p for p in path.parts if p.startswith('meta-')), '(root)')


def component_of(path):
    # For standard recipes-*/<component>/files/foo.patch, choose <component>.
    parts = path.parts
    for i, part in enumerate(parts[:-1]):
        if part.startswith('recipes-') and i + 1 < len(parts) - 1:
            return parts[i + 1]
    return path.parent.name


def main():
    parser = argparse.ArgumentParser(
        description='Report Git patch age and upstreaming status from a meta-arm checkout.',
        epilog='''Examples:
  scripts/patch-history.py
  scripts/patch-history.py --verbose --sort age
  scripts/patch-history.py --verbose --sort status
  scripts/patch-history.py --layer meta-arm-bsp
  scripts/patch-history.py --output bsp-patches.csv

Only Git-tracked .patch and .diff files are scanned. Git rename history is
followed where possible. Ages are calculated from the oldest and newest
commits touching each file, not from patch header dates. By default, a
summary is printed to the terminal. --verbose also lists each patch and
its age. --output writes CSV for spreadsheet use; otherwise no file is
created. No files in the tree are changed.''',
        formatter_class=argparse.RawDescriptionHelpFormatter,
    )
    parser.add_argument('--layer', help='Restrict scanning to this layer/directory relative to repository root')
    parser.add_argument('--output', '-o', help='Write CSV to this path (no CSV is written by default)')
    parser.add_argument('--sort', choices=['path', 'age', 'updated', 'component', 'status'], default='path',
                        help='Patch listing and CSV row order (status: grouped alphabetically, oldest first within each status; age and updated: oldest first)')
    parser.add_argument('--summary', action='store_true', help='Print aggregate statistics (already the default; retained for compatibility)')
    parser.add_argument('--verbose', '-v', action='store_true', help='List every patch with its age and upstream status in the terminal')
    args = parser.parse_args()

    try:
        root = Path(subprocess.check_output(['git', 'rev-parse', '--show-toplevel'], stderr=subprocess.PIPE,
                                            text=True).strip()).resolve()
    except subprocess.CalledProcessError:
        parser.error('run this from inside a Git repository')
    if args.layer:
        target = (root / args.layer).resolve()
        if not target.is_relative_to(root) or not target.is_dir():
            parser.error('--layer must name an existing directory inside the repository')
        scope = target.relative_to(root).as_posix()
    else:
        scope = None

    try:
        files = git(root, 'ls-files', '-z', '--', *( [scope] if scope else [] )).split(b'\0')
    except RuntimeError as exc:
        parser.error(str(exc))
    today = dt.date.today()
    rows = []
    for encoded in files:
        if not encoded:
            continue
        relative = encoded.decode('utf-8', 'surrogateescape')
        if Path(relative).suffix.lower() not in ('.patch', '.diff'):
            continue
        absolute = root / relative
        if not absolute.is_file():
            continue
        try:
            added, updated, changes = metadata(root, relative)
        except RuntimeError as exc:
            print(f'warning: history unavailable for {relative}: {exc}', file=sys.stderr)
            added, updated, changes = '', '', 0
        status, detail = upstream_status(absolute)
        rows.append({
            'Patch Added': added,
            'Last Updated': updated,
            'Times Changed': changes,
            'Component': component_of(Path(relative)),
            'Upstream Status': status,
            'Patch Age (Days)': (today - dt.date.fromisoformat(added)).days if added else '',
            'Days Since Update': (today - dt.date.fromisoformat(updated)).days if updated else '',
            'Layer': layer_of(Path(relative)),
            'Patch File': relative,
            'Upstream Status Detail': detail,
        })
    keys = {
        'path': lambda r: r['Patch File'],
        'age': lambda r: (-(r['Patch Age (Days)'] if isinstance(r['Patch Age (Days)'], int) else -1), r['Patch File']),
        'updated': lambda r: (-(r['Days Since Update'] if isinstance(r['Days Since Update'], int) else -1), r['Patch File']),
        'component': lambda r: (r['Component'], r['Patch File']),
        'status': lambda r: (r['Upstream Status'].lower(), -(r['Patch Age (Days)'] if isinstance(r['Patch Age (Days)'], int) else -1), r['Patch File']),
    }
    rows.sort(key=keys[args.sort])
    if args.output:
        output = Path(args.output)
        fields = ['Patch Added', 'Last Updated', 'Times Changed', 'Component', 'Upstream Status',
                  'Patch Age (Days)', 'Days Since Update', 'Layer', 'Patch File', 'Upstream Status Detail']
        try:
            with output.open('w', newline='', encoding='utf-8') as stream:
                writer = csv.DictWriter(stream, fieldnames=fields)
                writer.writeheader()
                writer.writerows(rows)
        except OSError as exc:
            parser.exit(1, f'error: cannot write {output}: {exc}\n')
        print(f'Wrote {len(rows)} patches to {output}')

    if args.verbose:
        print('\nPatch Details (age in days)')
        print('===========================')
        for row in rows:
            age = row['Patch Age (Days)']
            print(f"{str(age) if age != '' else 'n/a':>7}  {row['Upstream Status']:<16}  {row['Patch File']}")

    if True:  # Summary is the default terminal output.
        ages = [r['Patch Age (Days)'] for r in rows if isinstance(r['Patch Age (Days)'], int)]
        print('\nPatch Maintenance Summary')
        print('=========================')
        print(f'Total patches:              {len(rows)}')
        if ages:
            print(f'Average patch age:          {sum(ages) / len(ages):.0f} days')
            print(f'Oldest patch:               {max(ages)} days')
            print(f'Patches older than 1 year:  {sum(a > 365 for a in ages)}')
            print(f'Patches older than 2 years: {sum(a > 730 for a in ages)}')
        counts = Counter(r['Upstream Status'] for r in rows)
        groups = defaultdict(list)
        for r in rows:
            if isinstance(r['Patch Age (Days)'], int):
                groups[r['Upstream Status']].append(r['Patch Age (Days)'])
        print('\nUpstream Status (count, average age in days):')
        for status in sorted(counts, key=str.lower):
            ages_for_status = groups[status]
            average = f'{sum(ages_for_status) / len(ages_for_status):.0f}' if ages_for_status else 'n/a'
            print(f'  {status:<20} {counts[status]:>5}  {average:>6}')


if __name__ == '__main__':
    main()
