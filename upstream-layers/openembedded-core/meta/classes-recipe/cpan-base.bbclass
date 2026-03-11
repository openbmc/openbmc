#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

#
# cpan-base providers various perl related information needed for building
# cpan modules
#
FILES:${PN} += "${libdir}/perl5 ${datadir}/perl5"

DEPENDS  += "${@["perl", "perl-native"][(bb.data.inherits_class('native', d))]}"
RDEPENDS:${PN} += "${@["perl", ""][(bb.data.inherits_class('native', d))]}"

inherit perl-version

def is_target(d):
    if not bb.data.inherits_class('native', d):
        return "yes"
    return "no"

PERLLIBDIRS = "${libdir}/perl5"
PERLLIBDIRS:class-native = "${libdir}/perl5"

def cpan_upstream_check_pattern(d):
    for x in (d.getVar('SRC_URI') or '').split(' '):
        if x.startswith("https://cpan.metacpan.org"):
            _pattern = x.split('/')[-1].replace(d.getVar('PV'), r'(?P<pver>\d+.\d+)')
            return _pattern
    return ''

UPSTREAM_CHECK_REGEX ?= "${@cpan_upstream_check_pattern(d)}"
