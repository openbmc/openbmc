require dpkg.inc
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI += "file://noman.patch \
            file://remove-tar-no-timestamp.patch \
            file://fix-abs-redefine.patch \
            file://arch_pm.patch \
            file://dpkg-configure.service \
            file://glibc2.5-sync_file_range.patch \
            file://add_armeb_triplet_entry.patch \
	    file://0002-Adapt-to-linux-wrs-kernel-version-which-has-characte.patch \
	    file://0003-Our-pre-postinsts-expect-D-to-be-set-when-running-in.patch \
	    file://0004-The-lutimes-function-doesn-t-work-properly-for-all-s.patch \
	    file://0005-dpkg-compiler.m4-remove-Wvla.patch \
        file://CVE-2015-0860.patch \
           "

SRC_URI[md5sum] = "63b9d869081ec49adeef6c5ff62d6576"
SRC_URI[sha256sum] = "11484f2a73d027d696e720a60380db71978bb5c06cd88fe30c291e069ac457a4"

