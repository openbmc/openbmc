SRC_URI = "${GNU_MIRROR}/wget/wget-${PV}.tar.gz \
           file://0001-Unset-need_charset_alias-when-building-for-musl.patch \
          "

SRC_URI[md5sum] = "fc2debd8399e3b933a9b226794e2a886"
SRC_URI[sha256sum] = "a00a65fab84cc46e24c53ce88c45604668a7a479276e037dc2f558e34717fb2d"

require wget.inc
