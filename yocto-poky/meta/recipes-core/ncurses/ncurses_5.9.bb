require ncurses.inc

REVISION = "20150329"

PR = "${INC_PR}.1"

SRC_URI += "file://tic-hang.patch \
            file://config.cache \
"
S = "${WORKDIR}/${BP}-${REVISION}"
SRC_URI[md5sum] = "cee991d09e69e60ebedef424804c52d4"
SRC_URI[sha256sum] = "5b64f40e4dce73e3aa83d15bd9257c6eff8790ec41150f0938bd87c0eb75828f"
