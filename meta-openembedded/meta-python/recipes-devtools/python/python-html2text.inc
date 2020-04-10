SUMMARY = "Convert HTML to Markdown-formatted text"
HOMEPAGE = "https://github.com/Alir3z4/html2text"

LICENSE = "GPL-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI[md5sum] = "21aad7ec95b70606024b783c8253899c"
SRC_URI[sha256sum] = "f516b9c10284174e2a974d86f91cab02b3cf983a17752075da751af0e895ef5e"

inherit pypi

BBCLASSEXTEND = "native nativesdk"
