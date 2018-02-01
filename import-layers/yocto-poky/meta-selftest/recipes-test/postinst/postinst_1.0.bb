LICENSE = "MIT"
ALLOW_EMPTY_${PN}-at-rootfs = "1"
ALLOW_EMPTY_${PN}-delayed-a = "1"
ALLOW_EMPTY_${PN}-delayed-b = "1"
ALLOW_EMPTY_${PN}-delayed-d = "1"
ALLOW_EMPTY_${PN}-delayed-p = "1"
ALLOW_EMPTY_${PN}-delayed-t = "1"

PACKAGES += "${PN}-at-rootfs ${PN}-delayed-a ${PN}-delayed-b ${PN}-delayed-d ${PN}-delayed-p ${PN}-delayed-t"
PROVIDES += "${PN}-at-rootfs ${PN}-delayed-a ${PN}-delayed-b ${PN}-delayed-d ${PN}-delayed-p ${PN}-delayed-t"
FILES_${PN}-delayed-a = ""
FILES_${PN}-delayed-b = ""
FILES_${PN}-delayed-d = ""
FILES_${PN}-delayed-p = ""
FILES_${PN}-delayed-t = ""

# Runtime dependencies
RDEPENDS_${PN}-delayed-a = "${PN}-at-rootfs"
RDEPENDS_${PN}-delayed-b = "${PN}-delayed-a"
RDEPENDS_${PN}-delayed-d = "${PN}-delayed-b"
RDEPENDS_${PN}-delayed-p = "${PN}-delayed-d"
RDEPENDS_${PN}-delayed-t = "${PN}-delayed-p"

# Main recipe post-install
pkg_postinst_${PN}-at-rootfs () {
    tfile="/etc/postinsta-test"
    touch "$D"/this-was-created-at-rootfstime
    if test "x$D" != "x" then
        # Need to run on first boot
        exit 1
    else
        echo "lets write postinst" > $tfile
    fi
}

# Dependency recipes post-installs
pkg_postinst_${PN}-delayed-a () {
    efile="/etc/postinst-test"
    tfile="/etc/postinsta-test"
    rdeps="postinst"

    if test "x$D" != "x"; then
      # Need to run on first boot
      exit 1
    else
      touch /etc/this-was-created-at-first-boot
      if test -e $efile ; then
        echo 'success' > $tfile
      else
        echo 'fail to install $rdeps first!' >&2
        exit 1
      fi
   fi
}

pkg_postinst_${PN}-delayed-b () {
    efile="/etc/postinsta-test"
    tfile="/etc/postinstb-test"
    rdeps="postinsta"

    if test "x$D" != "x"; then
      # Need to run on first boot
      exit 1
    else
      if test -e $efile ; then
        echo 'success' > $tfile
      else
        echo 'fail to install $rdeps first!' >&2
        exit 1
      fi
   fi
}

pkg_postinst_${PN}-delayed-d () {
    efile="/etc/postinstb-test"
    tfile="/etc/postinstd-test"
    rdeps="postinstb"

    if test "x$D" != "x"; then
      # Need to run on first boot
      exit 1
    else
      if test -e $efile ; then
        echo 'success' > $tfile
      else
        echo 'fail to install $rdeps first!' >&2
        exit 1
      fi
   fi
}

pkg_postinst_${PN}-delayed-p () {
    efile="/etc/postinstd-test"
    tfile="/etc/postinstp-test"
    rdeps="postinstd"

    if test "x$D" != "x"; then
      # Need to run on first boot
      exit 1
    else
      if test -e $efile ; then
        echo 'success' > $tfile
      else
        echo 'fail to install $rdeps first!' >&2
        exit 1
      fi
   fi
}

pkg_postinst_${PN}-delayed-t () {
    efile="/etc/postinstp-test"
    tfile="/etc/postinstt-test"
    rdeps="postinstp"

    if test "x$D" != "x"; then
      # Need to run on first boot
      exit 1
    else
      if test -e $efile ; then
          echo 'success' > $tfile
      else
          echo 'fail to install $rdeps first!' >&2
          exit 1
      fi
   fi
}
