SUMMARY = "Packages required for a target (on-device) SDK"

PR = "r1"

inherit packagegroup

RPROVIDES:${PN} += "packagegroup-native-sdk"
RREPLACES:${PN} += "packagegroup-native-sdk"
RCONFLICTS:${PN} += "packagegroup-native-sdk"
RDEPENDS:${PN} = "gcc-symlinks g++-symlinks cpp cpp-symlinks \
                  binutils-symlinks \
                  perl-modules \
                  flex flex-dev \
                  bison \
                  gawk \
                  sed \
                  grep \
                  autoconf automake \
                  make \
                  patch diffstat diffutils \
                  libstdc++-dev \
                  libtool libtool-dev \
                  pkgconfig"

# usefull, but not in oe-core/meta-oe yet: patchutils
RRECOMMENDS:${PN} = " g77-symlinks gfortran-symlinks"
