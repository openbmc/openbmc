FILESEXTRAPATHS:prepend := "${THISDIR}/corstone1000/${PN}:"

SRC_URI:append:corstone1000 = " \
  file://0001-newlib-memcpy-remove-optimized-version.patch;patchdir=../newlib \
"

