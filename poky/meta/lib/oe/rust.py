#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# Handle mismatches between `uname -m`-style output and Rust's arch names
def arch_to_rust_arch(arch):
    if arch == "ppc64le":
        return "powerpc64le"
    if arch in ('riscv32', 'riscv64'):
        return arch + 'gc'
    return arch
