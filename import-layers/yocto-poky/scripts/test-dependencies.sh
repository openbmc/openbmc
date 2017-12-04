#!/bin/bash

# Author: Martin Jansa <martin.jansa@gmail.com>
#
# Copyright (c) 2013 Martin Jansa <Martin.Jansa@gmail.com>

# Used to detect missing dependencies or automagically
# enabled dependencies which aren't explicitly enabled
# or disabled. Using bash to have PIPESTATUS variable.

# It does 3 builds of <target>
# 1st to populate sstate-cache directory and sysroot
# 2nd to rebuild each recipe with every possible
#     dependency found in sysroot (which stays populated
#     from 1st build
# 3rd to rebuild each recipe only with dependencies defined
#     in DEPENDS
# 4th (optional) repeat build like 3rd to make sure that
#     minimal versions of dependencies defined in DEPENDS
#     is also enough

# Global vars
tmpdir=
targets=
recipes=
buildhistory=
buildtype=
default_targets="world"
default_buildhistory="buildhistory"
default_buildtype="1 2 3 c"

usage () {
  cat << EOF
Welcome to utility to detect missing or autoenabled dependencies.
WARNING: this utility will completely remove your tmpdir (make sure
         you don't have important buildhistory or persistent dir there).
$0 <OPTION>

Options:
  -h, --help
        Display this help and exit.

  --tmpdir=<tmpdir>
        Specify tmpdir, will use the environment variable TMPDIR if it is not specified.
        Something like /OE/oe-core/tmp-eglibc (no / at the end).

  --targets=<targets>
        List of targets separated by space, will use the environment variable TARGETS if it is not specified.
        It will run "bitbake <targets>" to populate sysroots.
        Default value is "world".

  --recipes=<recipes>
        File with list of recipes we want to rebuild with minimal and maximal sysroot.
        Will use the environment variable RECIPES if it is not specified.
        Default value will use all packages ever recorded in buildhistory directory.

  --buildhistory=<buildhistory>
        Path to buildhistory directory, it needs to be enabled in your config,
        because it's used to detect different dependencies and to create list
        of recipes to rebuild when it's not specified.
        Will use the environment variable BUILDHISTORY if it is not specified.
        Default value is "buildhistory"

  --buildtype=<buildtype>
        There are 4 types of build:
          1: build to populate sstate-cache directory and sysroot
          2: build to rebuild each recipe with every possible dep
          3: build to rebuild each recipe with minimal dependencies
          4: build to rebuild each recipe again with minimal dependencies
          c: compare buildhistory directories from build 2 and 3
        Will use the environment variable BUILDTYPE if it is not specified.
        Default value is "1 2 3 c", order is important, type 4 is optional.
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
    --targets=*)
      targets=`echo $1 | sed -e 's#^--targets="*\([^"]*\)"*#\1#'`
      shift
        ;;
    --recipes=*)
      recipes=`echo $1 | sed -e 's#^--recipes="*\([^"]*\)"*#\1#'`
      shift
        ;;
    --buildhistory=*)
      buildhistory=`echo $1 | sed -e 's#^--buildhistory="*\([^"]*\)"*#\1#'`
      shift
        ;;
    --buildtype=*)
      buildtype=`echo $1 | sed -e 's#^--buildtype="*\([^"]*\)"*#\1#'`
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
[ -n "$targets" ] || targets=$TARGETS
[ -n "$targets" ] || targets=$default_targets
[ -n "$recipes" ] || recipes=$RECIPES
[ -n "$recipes" -a ! -f "$recipes" ] && echo_error "Invalid file with list of recipes to rebuild"
[ -n "$recipes" ] || echo "All packages ever recorded in buildhistory directory will be rebuilt"
[ -n "$buildhistory" ] || buildhistory=$BUILDHISTORY
[ -n "$buildhistory" ] || buildhistory=$default_buildhistory
[ -d "$buildhistory" ] || echo_error "Invalid buildhistory directory \"$buildhistory\""
[ -n "$buildtype" ] || buildtype=$BUILDTYPE
[ -n "$buildtype" ] || buildtype=$default_buildtype
echo "$buildtype" | grep -v '^[1234c ]*$' && echo_error "Invalid buildtype \"$buildtype\", only some combination of 1, 2, 3, 4, c separated by space is allowed"

OUTPUT_BASE=test-dependencies/`date "+%s"`
declare -i RESULT=0

build_all() {
  echo "===== 1st build to populate sstate-cache directory and sysroot ====="
  OUTPUT1=${OUTPUT_BASE}/${TYPE}_all
  mkdir -p ${OUTPUT1}
  echo "Logs will be stored in ${OUTPUT1} directory"
  bitbake -k $targets 2>&1 | tee -a ${OUTPUT1}/complete.log
  RESULT+=${PIPESTATUS[0]}
  grep "ERROR: Task.*failed" ${OUTPUT1}/complete.log > ${OUTPUT1}/failed-tasks.log
  cat ${OUTPUT1}/failed-tasks.log | sed 's@.*/@@g; s@_.*@@g; s@\.bb, .*@@g; s@\.bb:.*@@g' | sort -u > ${OUTPUT1}/failed-recipes.log
}

build_every_recipe() {
  if [ "${TYPE}" = "2" ] ; then
    echo "===== 2nd build to rebuild each recipe with every possible dep ====="
    OUTPUT_MAX=${OUTPUT_BASE}/${TYPE}_max
    OUTPUTB=${OUTPUT_MAX}
  else
    echo "===== 3rd or 4th build to rebuild each recipe with minimal dependencies ====="
    OUTPUT_MIN=${OUTPUT_BASE}/${TYPE}_min
    OUTPUTB=${OUTPUT_MIN}
  fi

  mkdir -p ${OUTPUTB} ${OUTPUTB}/failed ${OUTPUTB}/ok
  echo "Logs will be stored in ${OUTPUTB} directory"
  if [ -z "$recipes" ]; then
    ls -d $buildhistory/packages/*/* | xargs -n 1 basename | sort -u > ${OUTPUTB}/recipe.list
    recipes=${OUTPUTB}/recipe.list
  fi
  if [ "${TYPE}" != "2" ] ; then
    echo "!!!Removing tmpdir \"$tmpdir\"!!!"
    rm -rf $tmpdir/deploy $tmpdir/pkgdata $tmpdir/sstate-control $tmpdir/stamps $tmpdir/sysroots $tmpdir/work $tmpdir/work-shared 2>/dev/null
  fi
  i=1
  count=`cat $recipes ${OUTPUT1}/failed-recipes.log | sort -u | wc -l`
  for recipe in `cat $recipes ${OUTPUT1}/failed-recipes.log | sort -u`; do
    echo "Building recipe: ${recipe} ($i/$count)"
    declare -i RECIPE_RESULT=0
    bitbake -c cleansstate ${recipe} > ${OUTPUTB}/${recipe}.log 2>&1;
    RECIPE_RESULT+=$?
    bitbake ${recipe} >> ${OUTPUTB}/${recipe}.log 2>&1;
    RECIPE_RESULT+=$?
    if [ "${RECIPE_RESULT}" != "0" ] ; then
      RESULT+=${RECIPE_RESULT}
      mv ${OUTPUTB}/${recipe}.log ${OUTPUTB}/failed/
      grep "ERROR: Task.*failed"  ${OUTPUTB}/failed/${recipe}.log | tee -a ${OUTPUTB}/failed-tasks.log
      grep "ERROR: Task.*failed"  ${OUTPUTB}/failed/${recipe}.log | sed 's@.*/@@g; s@_.*@@g; s@\.bb, .*@@g; s@\.bb:.*@@g' >> ${OUTPUTB}/failed-recipes.log
      # and append also ${recipe} in case the failed task was from some dependency
      echo ${recipe} >> ${OUTPUTB}/failed-recipes.log
    else
      mv ${OUTPUTB}/${recipe}.log ${OUTPUTB}/ok/
    fi
    if [ "${TYPE}" != "2" ] ; then
      rm -rf $tmpdir/deploy $tmpdir/pkgdata $tmpdir/sstate-control $tmpdir/stamps $tmpdir/sysroots $tmpdir/work $tmpdir/work-shared 2>/dev/null
    fi
    i=`expr $i + 1`
  done
  echo "Copying buildhistory/packages to ${OUTPUTB}"
  cp -ra $buildhistory/packages ${OUTPUTB}
  # This will be usefull to see which library is pulling new dependency
  echo "Copying do_package logs to ${OUTPUTB}/do_package/"
  mkdir ${OUTPUTB}/do_package
  find $tmpdir/work/ -name log.do_package 2>/dev/null| while read f; do
    # pn is 3 levels back, but we don't know if there is just one log per pn (only one arch and version)
    # dest=`echo $f | sed 's#^.*/\([^/]*\)/\([^/]*\)/\([^/]*\)/log.do_package#\1#g'`
    dest=`echo $f | sed "s#$tmpdir/work/##g; s#/#_#g"`
    cp $f ${OUTPUTB}/do_package/$dest
  done
}

compare_deps() {
  # you can run just compare task with command like this
  # OUTPUT_BASE=test-dependencies/1373140172 \
  # OUTPUT_MAX=${OUTPUT_BASE}/2_max \
  # OUTPUT_MIN=${OUTPUT_BASE}/3_min \
  # openembedded-core/scripts/test-dependencies.sh --tmpdir=tmp-eglibc --targets=glib-2.0 --recipes=recipe_list --buildtype=c
  echo "===== Compare dependencies recorded in \"${OUTPUT_MAX}\" and \"${OUTPUT_MIN}\" ====="
  [ -n "${OUTPUTC}" ] || OUTPUTC=${OUTPUT_BASE}/comp
  mkdir -p ${OUTPUTC}
  OUTPUT_FILE=${OUTPUTC}/dependency-changes
  echo "Differences will be stored in ${OUTPUT_FILE}, dot is shown for every 100 of checked packages"
  echo > ${OUTPUT_FILE}

  [ -d ${OUTPUT_MAX} ] || echo_error "Directory with output from build 2 \"${OUTPUT_MAX}\" does not exist"
  [ -d ${OUTPUT_MIN} ] || echo_error "Directory with output from build 3 \"${OUTPUT_MIN}\" does not exist"
  [ -d ${OUTPUT_MAX}/packages/ ] || echo_error "Directory with packages from build 2 \"${OUTPUT_MAX}/packages/\" does not exist"
  [ -d ${OUTPUT_MIN}/packages/ ] || echo_error "Directory with packages from build 3 \"${OUTPUT_MIN}/packages/\" does not exist"
  i=0
  find ${OUTPUT_MAX}/packages/ -name latest | sed "s#${OUTPUT_MAX}/##g" | while read pkg; do
    max_pkg=${OUTPUT_MAX}/${pkg}
    min_pkg=${OUTPUT_MIN}/${pkg}
    # pkg=packages/armv5te-oe-linux-gnueabi/libungif/libungif/latest
    recipe=`echo "${pkg}" | sed 's#packages/[^/]*/\([^/]*\)/\([^/]*\)/latest#\1#g'`
    package=`echo "${pkg}" | sed 's#packages/[^/]*/\([^/]*\)/\([^/]*\)/latest#\2#g'`
    if [ ! -f "${min_pkg}" ] ; then
      echo "ERROR: ${recipe}: ${package} package isn't created when building with minimal dependencies?" | tee -a ${OUTPUT_FILE}
      echo ${recipe} >> ${OUTPUTC}/failed-recipes.log
      continue
    fi
    # strip version information in parenthesis
    max_deps=`grep "^RDEPENDS = " ${max_pkg} | sed 's/^RDEPENDS = / /g; s/$/ /g; s/([^(]*)//g'`
    min_deps=`grep "^RDEPENDS = " ${min_pkg} | sed 's/^RDEPENDS = / /g; s/$/ /g; s/([^(]*)//g'`
    if [ "$i" = 100 ] ; then
      echo -n "." # cheap progressbar
      i=0
    fi
    if [ "${max_deps}" = "${min_deps}" ] ; then
      # it's annoying long, but at least it's showing some progress, warnings are grepped at the end
      echo "NOTE: ${recipe}: ${package} rdepends weren't changed" >> ${OUTPUT_FILE}
    else
      missing_deps=
      for dep in ${max_deps}; do
        if ! echo "${min_deps}" | grep -q " ${dep} " ; then
          missing_deps="${missing_deps} ${dep}"
          echo # to get rid of dots on last line
          echo "WARN: ${recipe}: ${package} rdepends on ${dep}, but it isn't a build dependency?" | tee -a ${OUTPUT_FILE}
        fi
      done
      if [ -n "${missing_deps}" ] ; then
        echo ${recipe} >> ${OUTPUTC}/failed-recipes.log
      fi
    fi
    i=`expr $i + 1`
  done
  echo # to get rid of dots on last line
  echo "Found differences: "
  grep "^WARN: " ${OUTPUT_FILE} | tee ${OUTPUT_FILE}.warn.log
  echo "Found errors: "
  grep "^ERROR: " ${OUTPUT_FILE} | tee ${OUTPUT_FILE}.error.log
  RESULT+=`cat ${OUTPUT_FILE}.warn.log | wc -l`
  RESULT+=`cat ${OUTPUT_FILE}.error.log | wc -l`
}

for TYPE in $buildtype; do
  case ${TYPE} in
    1) build_all;;
    2) build_every_recipe;;
    3) build_every_recipe;;
    4) build_every_recipe;;
    c) compare_deps;;
    *) echo_error "Invalid buildtype \"$TYPE\""
  esac
done

cat ${OUTPUT_BASE}/*/failed-recipes.log | sort -u >> ${OUTPUT_BASE}/failed-recipes.log

if [ "${RESULT}" != "0" ] ; then
  echo "ERROR: ${RESULT} issues were found in these recipes: `cat ${OUTPUT_BASE}/failed-recipes.log | xargs`"
fi

echo "INFO: Output written in: ${OUTPUT_BASE}"
exit ${RESULT}
