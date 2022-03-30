SUMMARY = "Additional (old) themes for Xfwm4"
SECTION = "x11/wm"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit xfce

SRC_URI = "http://archive.xfce.org/src/art/${BPN}/${@'${PV}'[0:4]}/${BP}.tar.bz2"
SRC_URI[md5sum] = "eaa58362053a3549c8be0b32efd3c54f"
SRC_URI[sha256sum] = "3214d5f00e9703b5e8c9e7c3287d606dedec7285ceb4d5db332e93ada66fd575"

# using xfwm4-theme as in xfwm4 might cause warnings of packages supplied by
# multiple providers. So we use xfwm4-old-theme here.
python populate_packages:prepend () {
    themedir = d.expand('${datadir}/themes')
    do_split_packages(d, themedir, '^(.*)', 'xfwm4-old-theme-%s', 'XFWM4 theme %s', allow_dirs=True)
}

PACKAGES_DYNAMIC += "^xfwm4-old-theme-.*"
