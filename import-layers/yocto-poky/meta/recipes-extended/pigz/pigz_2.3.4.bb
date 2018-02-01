require pigz.inc
LIC_FILES_CHKSUM = "file://pigz.c;beginline=7;endline=21;md5=a21d4075cb00ab4ca17fce5e7534ca95"

UPSTREAM_CHECK_URI = "http://zlib.net/${BPN}/"
UPSTREAM_CHECK_REGEX = "pigz-(?P<pver>.*)\.tar"
SRC_URI = "https://github.com/madler/pigz/archive/v${PV}.tar.gz;downloadfilename=${BP}.tar.gz"
SRC_URI[md5sum] = "c109057050b15edf3eb9bb4d0805235e"
SRC_URI[sha256sum] = "763f2fdb203aa0b7b640e63385e38e5dd4e5aaa041bc8e42aa96f2ef156b06e8"

BBCLASSEXTEND = "native nativesdk"

