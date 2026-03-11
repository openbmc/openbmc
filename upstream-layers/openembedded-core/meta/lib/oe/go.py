#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import re

def map_arch(a):
    """
    Map our architecture names to Go's GOARCH names.
    See https://github.com/golang/go/blob/master/src/internal/syslist/syslist.go for the complete list.
    """
    if re.match('i.86', a):
        return '386'
    elif a == 'x86_64':
        return 'amd64'
    elif re.match('arm.*', a):
        return 'arm'
    elif re.match('aarch64.*', a):
        return 'arm64'
    elif re.match('mips64el.*', a):
        return 'mips64le'
    elif re.match('mips64.*', a):
        return 'mips64'
    elif a == 'mips':
        return 'mips'
    elif a == 'mipsel':
        return 'mipsle'
    elif re.match('p(pc|owerpc)(64le)', a):
        return 'ppc64le'
    elif re.match('p(pc|owerpc)(64)', a):
        return 'ppc64'
    elif a == 'riscv64':
        return 'riscv64'
    elif a == 'loongarch64':
        return 'loong64'
    raise KeyError(f"Cannot map architecture {a}")
