require dpkg.inc
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI_append_class-native =" file://glibc2.5-sync_file_range.patch "
SRC_URI += "file://noman.patch \
            file://remove-tar-no-timestamp.patch \
            file://fix-abs-redefine.patch \
            file://arch_pm.patch \
            file://dpkg-configure.service \
            file://add_armeb_triplet_entry.patch \
	    file://0002-Adapt-to-linux-wrs-kernel-version-which-has-characte.patch \
	    file://0003-Our-pre-postinsts-expect-D-to-be-set-when-running-in.patch \
	    file://0004-The-lutimes-function-doesn-t-work-properly-for-all-s.patch \
	    file://0005-dpkg-compiler.m4-remove-Wvla.patch \
	    file://0006-add-musleabi-to-known-target-tripets.patch \
           "

SRC_URI[md5sum] = "e95b513c89693f6ec3ab53b6b1c3defd"
SRC_URI[sha256sum] = "fe89243868888ce715bf45861f26264f767d4e4dbd0d6f1a26ce60bbbbf106da"

