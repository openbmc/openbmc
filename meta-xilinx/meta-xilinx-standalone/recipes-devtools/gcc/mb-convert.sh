#! /bin/bash

# Call using:
#../microblaze/sysroots/x86_64-oesdk-linux/usr/bin/microblaze-xilinx-elf/microblaze-xilinx-elf-gcc -print-multi-lib | mb-convert.sh

# Then copy the output into the special microblaze-tc BSP

mlib_to_feature() {
  feature_base="microblaze"
  feature_endian=" bigendian"
  feature_barrel=""
  feature_pattern=""
  feature_multiply=""
  feature_multiplyhigh=""
  feature_sixtyfour=""
  feature_math=""
  while read feature ; do
     case $feature in
       le) feature_endian="";;
       bs) feature_barrel=" barrel-shift";;
       p)  feature_pattern=" pattern-compare";;
       m)  if [ -z ${feature_multiplyhigh} ]; then feature_multiply=" multiply-low" ; fi ;;
       mh) feature_multiply="" ; feature_multiplyhigh=" multiply-high";;
       m64) feature_sixtyfour=" 64-bit";;
       fpd) feature_math=" fpu-hard";;
       *)   echo "UNKNOWN $feature";;
     esac
  done
  echo "${feature_base}${feature_sixtyfour}${feature_endian}${feature_barrel}${feature_pattern}${feature_multiply}${feature_multiplyhigh}${feature_math}"
}

sed -e 's,;, ,' |
  while read mlib args ; do
    if [ $mlib = '.' ]; then
      echo '# Base configuration'
      echo '# CFLAGS:'
      echo 'DEFAULTTUNE = "microblaze"'
      echo
      echo 'AVAILTUNES += "microblaze"'
      echo 'BASE_LIB_tune-microblaze = "lib"'
      echo 'TUNE_FEATURES_tune-microblaze = "microblaze bigendian"'
      echo 'PACKAGE_EXTRA_ARCHS_tune-microblaze = "${TUNE_PKGARCH}"'
      continue
    fi

    cflags=$(echo $args | sed -e 's,@, -,g')
    multilib="libmb$(echo $mlib | sed -e 's,/,,g')"
    tune="microblaze$(echo $mlib | sed -e 's,m64,64,' -e 's,/,,g')"
    features=$(echo $mlib | sed -e 's,/, ,g' | xargs -n 1 echo | mlib_to_feature)
    echo
    echo
    echo "# $mlib"
    echo "# CFLAGS:${cflags}"
    echo "DEFAULTTUNE_virtclass-multilib-$multilib = \"$tune\""
    echo
    echo "AVAILTUNES += \"$tune\""
    echo "BASE_LIB_tune-$tune = \"lib/$mlib\""
    echo "TUNE_FEATURES_tune-$tune = \"${features}\""
    echo "PACKAGE_EXTRA_ARCHS_tune-$tune = \"\${TUNE_PKGARCH}\""
  done
