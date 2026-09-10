#!/usr/bin/env python3
#
# Helpers for preparing pseudo-managed rootfs trees (NFS booting, SDK/rootfs
# extraction, target deploy).
#
# SPDX-License-Identifier: GPL-2.0-only

"""Extract rootfs tarballs and locate their pseudo state for NFS booting."""

import os
import subprocess
from pathlib import Path


class PseudoRootfsError(Exception):
    """Raised when a pseudo-managed rootfs cannot be prepared or exported."""


def pseudo_native_environment():
    """Return the pseudo native environment (PSEUDO and OECORE_NATIVE_SYSROOT), from the
    qemu-helper-native recipe which provides a pseudo binary usable outside a recipe sysroot."""
    native_sysroot = os.environ.get('OECORE_NATIVE_SYSROOT')
    if not native_sysroot:
        try:
            import bb.tinfoil
        except ImportError as exc:
            raise PseudoRootfsError(
                'Unable to import bitbake.\n'
                'Did you forget to source your build system environment setup script?') from exc
        try:
            with bb.tinfoil.Tinfoil() as tinfoil:
                tinfoil.prepare(quiet=2)
                native_sysroot = tinfoil.parse_recipe('qemu-helper-native').getVar('STAGING_DIR_NATIVE')
        except Exception as exc:
            raise PseudoRootfsError('Unable to set up the qemu-helper-native sysroot') from exc

    if not native_sysroot or not os.path.exists(native_sysroot):
        raise PseudoRootfsError("%s doesn't exist" % native_sysroot)

    environment = {'OECORE_NATIVE_SYSROOT': native_sysroot}
    environment['PSEUDO'] = os.path.join(native_sysroot, 'usr', 'bin', 'pseudo')
    return environment


def _tar_options(rootfs_tarball):
    tar_extract_options = {
        '.tar.xz': '-xJf',
        '.tar.bz2': '-xjf',
        '.tar.gz': '-xzf',
        '.tar.zst': '--zstd -xf',
        '.tar': '-xf',
    }
    for extension, option in tar_extract_options.items():
        if rootfs_tarball.endswith(extension):
            return ['--numeric-owner', *option.split()]
    raise PseudoRootfsError(
        'Unable to determine sdk tarball format\n'
        'Accepted types: .tar / .tar.gz / .tar.bz2 / .tar.xz / .tar.zst')


def pseudo_state_dir(rootfs_dir):
    """Return the pseudo database location associated with an extracted rootfs."""
    return os.path.realpath(rootfs_dir) + '.pseudo_state'


def extract_sdk_rootfs(rootfs_tarball, rootfs_dir, pseudo_cmd, environment):
    """Extract a rootfs tarball under pseudo and return its absolute directory.

    pseudo_cmd is the pseudo invocation prefix, e.g. a recipe's own
    [FAKEROOTCMD] or [pseudo, '-P', native_sysroot_usr] from
    pseudo_native_environment(). environment supplies the matching pseudo
    database/env vars (e.g. a parsed FAKEROOTENV, or pseudo_native_environment()
    itself); PSEUDO_LOCALSTATEDIR/PSEUDO_INCLUDE_PATHS are always overridden
    here to point at rootfs_dir's own pseudo state, regardless of what's
    already set in environment.
    """
    if not os.path.exists(rootfs_tarball):
        raise PseudoRootfsError("sdk tarball '%s' does not exist" % rootfs_tarball)

    rootfs_tarball = os.path.realpath(rootfs_tarball)
    rootfs_dir = os.path.realpath(rootfs_dir)
    tar_options = _tar_options(rootfs_tarball)
    state_dir = pseudo_state_dir(rootfs_dir)
    debug_image = '-dbg' in os.path.basename(rootfs_tarball)

    if os.path.exists(state_dir) and not debug_image:
        raise PseudoRootfsError(
            '%s already exists!\n'
            'Please delete the rootfs tree and pseudo directory manually\n'
            'if this is really what you want.' % state_dir)

    os.makedirs(rootfs_dir, exist_ok=True)
    os.makedirs(state_dir, exist_ok=True)
    Path(state_dir, 'pseudo.pid').touch()

    environment = dict(environment)
    environment['PSEUDO_LOCALSTATEDIR'] = state_dir
    environment['PSEUDO_INCLUDE_PATHS'] = rootfs_dir

    command = list(pseudo_cmd) + ['tar', '-C', rootfs_dir] + tar_options + [rootfs_tarball]
    print('Extracting rootfs tarball using pseudo...')
    print(' '.join(command))
    try:
        subprocess.run(command, env=environment, check=True)
    except subprocess.CalledProcessError as exc:
        raise PseudoRootfsError('Failed to extract rootfs tarball') from exc

    if len(os.listdir(rootfs_dir)) < 4:
        print("Warning: I don't see many files in %s" % rootfs_dir)
        print('Please double-check the extraction worked as intended')
    else:
        print('SDK image successfully extracted to %s' % rootfs_dir)
    return rootfs_dir
