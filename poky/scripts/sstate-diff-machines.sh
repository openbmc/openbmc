#!/bin/bash
#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#
# Used to compare sstate checksums between MACHINES.
# Execute script and compare generated list.M files.
# Using bash to have PIPESTATUS variable.

# It's also usefull to keep older sstate checksums
# to be able to find out why something is rebuilding
# after updating metadata

# $ diff \
#     sstate-diff/1349348392/fake-cortexa8/list.M \
#     sstate-diff/1349348392/fake-cortexa9/list.M \
#     | wc -l
# 538

# Then to compare sigdata use something like:
# $ ls sstate-diff/1349348392/*/armv7a-vfp-neon*/linux-libc-headers/*do_configure*sigdata*
#   sstate-diff/1349348392/fake-cortexa8/armv7a-vfp-neon-oe-linux-gnueabi/linux-libc-headers/3.4.3-r0.do_configure.sigdata.cb73b3630a7b8191e72fc469c5137025
#   sstate-diff/1349348392/fake-cortexa9/armv7a-vfp-neon-oe-linux-gnueabi/linux-libc-headers/3.4.3-r0.do_configure.sigdata.f37ada177bf99ce8af85914df22b5a0b
# $ bitbake-diffsigs stamps.1349348392/*/armv7a-vfp-neon*/linux-libc-headers/*do_configure*sigdata*
#   basehash changed from 8d0bd67bb1da6f68717760fc3ef43171 to e869fa61426e88e9c30726ba88a1216a
#   Variable TUNE_CCARGS value changed from  -march=armv7-a     -mthumb-interwork -mfloat-abi=softfp -mfpu=neon -mtune=cortex-a8 to  -march=armv7-a     -mthumb-interwork -mfloat-abi=softfp -mfpu=neon -mtune=cortex-a9

# Global vars
tmpdir=
machines=
targets=
default_machines="qemuarm qemux86 qemux86-64"
default_targets="core-image-base"
analyze="N"

usage () {
  cat << EOF
Welcome to utility to compare sstate checksums between different MACHINEs.
$0 <OPTION>

Options:
  -h, --help
        Display this help and exit.

  --tmpdir=<tmpdir>
        Specify tmpdir, will use the environment variable TMPDIR if it is not specified.
        Something like /OE/oe-core/tmp-eglibc (no / at the end).

  --machines=<machines>
        List of MACHINEs separated by space, will use the environment variable MACHINES if it is not specified.
        Default value is "qemuarm qemux86 qemux86-64".

  --targets=<targets>
        List of targets separated by space, will use the environment variable TARGETS if it is not specified.
        Default value is "core-image-base".

  --analyze
        Show the differences between MACHINEs. It assumes:
        * First 2 MACHINEs in --machines parameter have the same TUNE_PKGARCH
        * Third optional MACHINE has different TUNE_PKGARCH - only native and allarch recipes are compared).
        * Next MACHINEs are ignored
EOF
}

# Print error information and exit.
echo_error () {
  echo "ERROR: $1" >&2
  exit 1
}

while [ -n "$1" ]; do
  case $1 in
    --tmpdir=*)
      tmpdir=`echo $1 | sed -e 's#^--tmpdir=##' | xargs readlink -e`
      [ -d "$tmpdir" ] || echo_error "Invalid argument to --tmpdir"
      shift
        ;;
    --machines=*)
      machines=`echo $1 | sed -e 's#^--machines="*\([^"]*\)"*#\1#'`
      shift
        ;;
    --targets=*)
      targets=`echo $1 | sed -e 's#^--targets="*\([^"]*\)"*#\1#'`
      shift
        ;;
    --analyze)
      analyze="Y"
      shift
        ;;
    --help|-h)
      usage
      exit 0
        ;;
    *)
      echo "Invalid arguments $*"
      echo_error "Try '$0 -h' for more information."
        ;;
  esac
done

# tmpdir directory, use environment variable TMPDIR
# if it was not specified, otherwise, error.
[ -n "$tmpdir" ] || tmpdir=$TMPDIR
[ -n "$tmpdir" ] || echo_error "No tmpdir found!"
[ -d "$tmpdir" ] || echo_error "Invalid tmpdir \"$tmpdir\""
[ -n "$machines" ] || machines=$MACHINES
[ -n "$machines" ] || machines=$default_machines
[ -n "$targets" ] || targets=$TARGETS
[ -n "$targets" ] || targets=$default_targets

OUTPUT=${tmpdir}/sstate-diff/`date "+%s"`
declare -i RESULT=0

for M in ${machines}; do
  [ -d ${tmpdir}/stamps/ ] && find ${tmpdir}/stamps/ -name \*sigdata\* | xargs rm -f
  mkdir -p ${OUTPUT}/${M}
  export MACHINE=${M}
  bitbake -S none ${targets} 2>&1 | tee -a ${OUTPUT}/${M}/log;
  RESULT+=${PIPESTATUS[0]}
  if ls ${tmpdir}/stamps/* >/dev/null 2>/dev/null ; then
    cp -ra ${tmpdir}/stamps/* ${OUTPUT}/${M}
    find ${OUTPUT}/${M} -name \*sigdata\* | sed "s#${OUTPUT}/${M}/##g" | sort > ${OUTPUT}/${M}/list
    M_UNDERSCORE=`echo ${M} | sed 's/-/_/g'`
    sed "s/^${M_UNDERSCORE}-/MACHINE/g" ${OUTPUT}/${M}/list | sort > ${OUTPUT}/${M}/list.M
    find ${tmpdir}/stamps/ -name \*sigdata\* | xargs rm -f
  else
    printf "ERROR: no sigdata files were generated for MACHINE $M in ${tmpdir}/stamps\n";
  fi
done

COMPARE_TASKS="do_configure.sigdata do_populate_sysroot.sigdata do_package_write_ipk.sigdata do_package_write_rpm.sigdata do_package_write_deb.sigdata do_package_write_tar.sigdata"

function compareSignatures() {
  MACHINE1=$1
  MACHINE2=$2
  PATTERN="$3"
  PRE_PATTERN=""
  [ -n "${PATTERN}" ] || PRE_PATTERN="-v"
  [ -n "${PATTERN}" ] || PATTERN="MACHINE"
  for TASK in $COMPARE_TASKS; do
    printf "\n\n === Comparing signatures for task ${TASK} between ${MACHINE1} and ${MACHINE2} ===\n" | tee -a ${OUTPUT}/signatures.${MACHINE2}.${TASK}.log
    diff ${OUTPUT}/${MACHINE1}/list.M ${OUTPUT}/${MACHINE2}/list.M | grep ${PRE_PATTERN} "${PATTERN}" | grep ${TASK} > ${OUTPUT}/signatures.${MACHINE2}.${TASK}
    for i in `cat ${OUTPUT}/signatures.${MACHINE2}.${TASK} | sed 's#[^/]*/\([^/]*\)/.*#\1#g' | sort -u | xargs`; do
      [ -e ${OUTPUT}/${MACHINE1}/*/$i/*${TASK}* ] || echo "INFO: ${i} task ${TASK} doesn't exist in ${MACHINE1}" >&2
      [ -e ${OUTPUT}/${MACHINE1}/*/$i/*${TASK}* ] || continue
      [ -e ${OUTPUT}/${MACHINE2}/*/$i/*${TASK}* ] || echo "INFO: ${i} task ${TASK} doesn't exist in ${MACHINE2}" >&2
      [ -e ${OUTPUT}/${MACHINE2}/*/$i/*${TASK}* ] || continue
      printf "ERROR: $i different signature for task ${TASK} between ${MACHINE1} and ${MACHINE2}\n";
      bitbake-diffsigs ${OUTPUT}/${MACHINE1}/*/$i/*${TASK}* ${OUTPUT}/${MACHINE2}/*/$i/*${TASK}*;
      echo "$i" >> ${OUTPUT}/failed-recipes.log
      echo
    done | tee -a ${OUTPUT}/signatures.${MACHINE2}.${TASK}.log
    # don't create empty files
    ERRORS=`grep "^ERROR.*" ${OUTPUT}/signatures.${MACHINE2}.${TASK}.log | wc -l`
    if [ "${ERRORS}" != "0" ] ; then
      echo "ERROR: ${ERRORS} errors found in ${OUTPUT}/signatures.${MACHINE2}.${TASK}.log"
      RESULT+=${ERRORS}
    fi
  done
}

function compareMachines() {
  [ "$#" -ge 2 ] && compareSignatures $1 $2
  [ "$#" -ge 3 ] && compareSignatures $1 $3 "\(^< all\)\|\(^< x86_64-linux\)\|\(^< i586-linux\)"
}

if [ "${analyze}" = "Y" ] ; then
  compareMachines ${machines}
fi

if [ "${RESULT}" != "0" -a -f ${OUTPUT}/failed-recipes.log ] ; then
  cat ${OUTPUT}/failed-recipes.log | sort -u >${OUTPUT}/failed-recipes.log.u && mv ${OUTPUT}/failed-recipes.log.u ${OUTPUT}/failed-recipes.log
  echo "ERROR: ${RESULT} issues were found in these recipes: `cat ${OUTPUT}/failed-recipes.log | xargs`"
fi

echo "INFO: Output written in: ${OUTPUT}"
exit ${RESULT}
