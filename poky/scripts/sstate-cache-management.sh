#!/bin/bash

#  Copyright (c) 2012 Wind River Systems, Inc.
#
# SPDX-License-Identifier: GPL-2.0-only
#

# Global vars
cache_dir=
confirm=
fsym=
total_deleted=0
verbose=
debug=0

usage () {
  cat << EOF
Welcome to sstate cache management utilities.
sstate-cache-management.sh <OPTION>

Options:
  -h, --help
        Display this help and exit.

  --cache-dir=<sstate cache dir>
        Specify sstate cache directory, will use the environment
        variable SSTATE_CACHE_DIR if it is not specified.

  --extra-archs=<arch1>,<arch2>...<archn>
        Specify list of architectures which should be tested, this list
        will be extended with native arch, allarch and empty arch. The
        script won't be trying to generate list of available archs from
        AVAILTUNES in tune files.

  --extra-layer=<layer1>,<layer2>...<layern>
        Specify the layer which will be used for searching the archs,
        it will search the meta and meta-* layers in the top dir by
        default, and will search meta, meta-*, <layer1>, <layer2>,
        ...<layern> when specified. Use "," as the separator.

        This is useless for --stamps-dir or when --extra-archs is used.

  -d, --remove-duplicated
        Remove the duplicated sstate cache files of one package, only
        the newest one will be kept. The duplicated sstate cache files
        of one package must have the same arch, which means sstate cache
        files with multiple archs are not considered duplicate.

        Conflicts with --stamps-dir.

  --stamps-dir=<dir1>,<dir2>...<dirn>
        Specify the build directory's stamps directories, the sstate
        cache file which IS USED by these build diretories will be KEPT,
        other sstate cache files in cache-dir will be removed. Use ","
        as the separator. For example:
        --stamps-dir=build1/tmp/stamps,build2/tmp/stamps

        Conflicts with --remove-duplicated.

  -L, --follow-symlink
        Remove both the symbol link and the destination file, default: no.

  -y, --yes
        Automatic yes to prompts; assume "yes" as answer to all prompts
        and run non-interactively.

  -v, --verbose
        Explain what is being done.

  -D, --debug
        Show debug info, repeat for more debug info.

EOF
}

if [ $# -lt 1 ]; then
  usage
  exit 0
fi

# Echo no files to remove
no_files () {
    echo No files to remove
}

# Echo nothing to do
do_nothing () {
   echo Nothing to do
}

# Read the input "y"
read_confirm () {
  echo "$total_deleted out of $total_files files will be removed! "
  if [ "$confirm" != "y" ]; then
      echo "Do you want to continue (y/n)? "
      while read confirm; do
          [ "$confirm" = "Y" -o "$confirm" = "y" -o "$confirm" = "n" \
            -o "$confirm" = "N" ] && break
          echo "Invalid input \"$confirm\", please input 'y' or 'n': "
      done
  else
      echo
  fi
}

# Print error information and exit.
echo_error () {
  echo "ERROR: $1" >&2
  exit 1
}

# Generate the remove list:
#
# * Add .done/.siginfo to the remove list
# * Add destination of symlink to the remove list
#
# $1: output file, others: sstate cache file (.tar.zst)
gen_rmlist (){
  local rmlist_file="$1"
  shift
  local files="$@"
  for i in $files; do
      echo $i >> $rmlist_file
      # Add the ".siginfo"
      if [ -e $i.siginfo ]; then
          echo $i.siginfo >> $rmlist_file
      fi
      # Add the destination of symlink
      if [ -L "$i" ]; then
          if [ "$fsym" = "y" ]; then
              dest="`readlink -e $i`"
              if [ -n "$dest" ]; then
                  echo $dest >> $rmlist_file
                  # Remove the .siginfo when .tar.zst is removed
                  if [ -f "$dest.siginfo" ]; then
                      echo $dest.siginfo >> $rmlist_file
                  fi
              fi
          fi
          # Add the ".tar.zst.done" and ".siginfo.done" (may exist in the future)
          base_fn="${i##/*/}"
          t_fn="$base_fn.done"
          s_fn="$base_fn.siginfo.done"
          for d in $t_fn $s_fn; do
              if [ -f $cache_dir/$d ]; then
                  echo $cache_dir/$d >> $rmlist_file
              fi
          done
      fi
  done
}

# Remove the duplicated cache files for the pkg, keep the newest one
remove_duplicated () {

  local topdir
  local oe_core_dir
  local tunedirs
  local all_archs
  local all_machines
  local ava_archs
  local arch
  local file_names
  local sstate_files_list
  local fn_tmp
  local list_suffix=`mktemp` || exit 1

  if [ -z "$extra_archs" ] ; then
    # Find out the archs in all the layers
    echo "Figuring out the archs in the layers ... "
    oe_core_dir=$(dirname $(dirname $(readlink -e $0)))
    topdir=$(dirname $oe_core_dir)
    tunedirs="`find $topdir/meta* ${oe_core_dir}/meta* $layers -path '*/meta*/conf/machine/include' 2>/dev/null`"
    [ -n "$tunedirs" ] || echo_error "Can't find the tune directory"
    all_machines="`find $topdir/meta* ${oe_core_dir}/meta* $layers -path '*/meta*/conf/machine/*' -name '*.conf' 2>/dev/null | sed -e 's/.*\///' -e 's/.conf$//'`"
    all_archs=`grep -r -h "^AVAILTUNES .*=" $tunedirs | sed -e 's/.*=//' -e 's/\"//g'`
  fi

  # Use the "_" to substitute "-", e.g., x86-64 to x86_64, but not for extra_archs which can be something like cortexa9t2-vfp-neon
  # Sort to remove the duplicated ones
  # Add allarch and builder arch (native)
  builder_arch=$(uname -m)
  all_archs="$(echo allarch $all_archs $all_machines $builder_arch \
          | sed -e 's/-/_/g' -e 's/ /\n/g' | sort -u) $extra_archs"
  echo "Done"

  # Total number of files including sstate-, .siginfo and .done files
  total_files=`find $cache_dir -name 'sstate*' | wc -l`
  # Save all the sstate files in a file
  sstate_files_list=`mktemp` || exit 1
  find $cache_dir -iname 'sstate:*:*:*:*:*:*:*.tar.zst*' >$sstate_files_list

  echo "Figuring out the suffixes in the sstate cache dir ... "
  sstate_suffixes="`sed 's%.*/sstate:[^:]*:[^:]*:[^:]*:[^:]*:[^:]*:[^:]*:[^_]*_\([^:]*\)\.tar\.zst.*%\1%g' $sstate_files_list | sort -u`"
  echo "Done"
  echo "The following suffixes have been found in the cache dir:"
  echo $sstate_suffixes

  echo "Figuring out the archs in the sstate cache dir ... "
  # Using this SSTATE_PKGSPEC definition it's 6th colon separated field
  # SSTATE_PKGSPEC    = "sstate:${PN}:${PACKAGE_ARCH}${TARGET_VENDOR}-${TARGET_OS}:${PV}:${PR}:${SSTATE_PKGARCH}:${SSTATE_VERSION}:"
  for arch in $all_archs; do
      grep -q ".*/sstate:[^:]*:[^:]*:[^:]*:[^:]*:$arch:[^:]*:[^:]*\.tar\.zst$" $sstate_files_list
      [ $? -eq 0 ] && ava_archs="$ava_archs $arch"
      # ${builder_arch}_$arch used by toolchain sstate
      grep -q ".*/sstate:[^:]*:[^:]*:[^:]*:[^:]*:${builder_arch}_$arch:[^:]*:[^:]*\.tar\.zst$" $sstate_files_list
      [ $? -eq 0 ] && ava_archs="$ava_archs ${builder_arch}_$arch"
  done
  echo "Done"
  echo "The following archs have been found in the cache dir:"
  echo $ava_archs
  echo ""

  # Save the file list which needs to be removed
  local remove_listdir=`mktemp -d` || exit 1
  for suffix in $sstate_suffixes; do
      if [ "$suffix" = "populate_lic" ] ; then
          echo "Skipping populate_lic, because removing duplicates doesn't work correctly for them (use --stamps-dir instead)"
          continue
      fi
      # Total number of files including .siginfo and .done files
      total_files_suffix=`grep ".*/sstate:[^:]*:[^:]*:[^:]*:[^:]*:[^:]*:[^:]*:[^:_]*_$suffix\.tar\.zst.*" $sstate_files_list | wc -l 2>/dev/null`
      total_archive_suffix=`grep ".*/sstate:[^:]*:[^:]*:[^:]*:[^:]*:[^:]*:[^:]*:[^:_]*_$suffix\.tar\.zst$" $sstate_files_list | wc -l 2>/dev/null`
      # Save the file list to a file, some suffix's file may not exist
      grep ".*/sstate:[^:]*:[^:]*:[^:]*:[^:]*:[^:]*:[^:]*:[^:_]*_$suffix\.tar\.zst.*" $sstate_files_list >$list_suffix 2>/dev/null
      local deleted_archives=0
      local deleted_files=0
      for ext in tar.zst tar.zst.siginfo tar.zst.done; do
          echo "Figuring out the sstate:xxx_$suffix.$ext ... "
          # Uniq BPNs
          file_names=`for arch in $ava_archs ""; do
              sed -ne "s%.*/sstate:\([^:]*\):[^:]*:[^:]*:[^:]*:$arch:[^:]*:[^:]*\.${ext}$%\1%p" $list_suffix
          done | sort -u`

          fn_tmp=`mktemp` || exit 1
          rm_list="$remove_listdir/sstate:xxx_$suffix"
          for fn in $file_names; do
              [ -z "$verbose" ] || echo "Analyzing sstate:$fn-xxx_$suffix.${ext}"
              for arch in $ava_archs ""; do
                  grep -h ".*/sstate:$fn:[^:]*:[^:]*:[^:]*:$arch:[^:]*:[^:]*\.${ext}$" $list_suffix >$fn_tmp
                  if [ -s $fn_tmp ] ; then
                      [ $debug -gt 1 ] && echo "Available files for $fn-$arch- with suffix $suffix.${ext}:" && cat $fn_tmp
                      # Use the modification time
                      to_del=$(ls -t $(cat $fn_tmp) | sed -n '1!p')
                      [ $debug -gt 2 ] && echo "Considering to delete: $to_del"
                      # The sstate file which is downloaded from the SSTATE_MIRROR is
                      # put in SSTATE_DIR, and there is a symlink in SSTATE_DIR/??/ to
                      # it, so filter it out from the remove list if it should not be
                      # removed.
                      to_keep=$(ls -t $(cat $fn_tmp) | sed -n '1p')
                      [ $debug -gt 2 ] && echo "Considering to keep: $to_keep"
                      for k in $to_keep; do
                          if [ -L "$k" ]; then
                              # The symlink's destination
                              k_dest="`readlink -e $k`"
                              # Maybe it is the one in cache_dir
                              k_maybe="$cache_dir/${k##/*/}"
                              # Remove it from the remove list if they are the same.
                              if [ "$k_dest" = "$k_maybe" ]; then
                                  to_del="`echo $to_del | sed 's#'\"$k_maybe\"'##g'`"
                              fi
                          fi
                      done
                      rm -f $fn_tmp
                      [ $debug -gt 2 ] && echo "Decided to delete: $to_del"
                      gen_rmlist $rm_list.$ext "$to_del"
                  fi
              done
          done
      done
      deleted_archives=`cat $rm_list.* 2>/dev/null | grep "\.tar\.zst$" | wc -l`
      deleted_files=`cat $rm_list.* 2>/dev/null | wc -l`
      [ "$deleted_files" -gt 0 -a $debug -gt 0 ] && cat $rm_list.*
      echo "($deleted_archives out of $total_archives_suffix .tar.zst files for $suffix suffix will be removed or $deleted_files out of $total_files_suffix when counting also .siginfo and .done files)"
      let total_deleted=$total_deleted+$deleted_files
  done
  deleted_archives=0
  rm_old_list=$remove_listdir/sstate-old-filenames
  find $cache_dir -name 'sstate-*.tar.zst' >$rm_old_list
  [ -s "$rm_old_list" ] && deleted_archives=`cat $rm_old_list | grep "\.tar\.zst$" | wc -l`
  [ -s "$rm_old_list" ] && deleted_files=`cat $rm_old_list | wc -l`
  [ -s "$rm_old_list" -a $debug -gt 0 ] && cat $rm_old_list
  echo "($deleted_archives or .tar.zst files with old sstate-* filenames will be removed or $deleted_files when counting also .siginfo and .done files)"
  let total_deleted=$total_deleted+$deleted_files

  rm -f $list_suffix
  rm -f $sstate_files_list
  if [ $total_deleted -gt 0 ]; then
      read_confirm
      if [ "$confirm" = "y" -o "$confirm" = "Y" ]; then
          for list in `ls $remove_listdir/`; do
              echo "Removing $list.tar.zst archive (`cat $remove_listdir/$list | wc -w` files) ... "
              # Remove them one by one to avoid the argument list too long error
              for i in `cat $remove_listdir/$list`; do
                  rm -f $verbose $i
              done
              echo "Done"
          done
          echo "$total_deleted files have been removed!"
      else
          do_nothing
      fi
  else
       no_files
  fi
  [ -d $remove_listdir ] && rm -fr $remove_listdir
}

# Remove the sstate file by stamps dir, the file not used by the stamps dir
# will be removed.
rm_by_stamps (){

  local cache_list=`mktemp` || exit 1
  local keep_list=`mktemp` || exit 1
  local rm_list=`mktemp` || exit 1
  local sums
  local all_sums

  # Total number of files including sstate-, .siginfo and .done files
  total_files=`find $cache_dir -type f -name 'sstate*' | wc -l`
  # Save all the state file list to a file
  find $cache_dir -type f -name 'sstate*' | sort -u -o $cache_list

  echo "Figuring out the suffixes in the sstate cache dir ... "
  local sstate_suffixes="`sed 's%.*/sstate:[^:]*:[^:]*:[^:]*:[^:]*:[^:]*:[^:]*:[^_]*_\([^:]*\)\.tar\.zst.*%\1%g' $cache_list | sort -u`"
  echo "Done"
  echo "The following suffixes have been found in the cache dir:"
  echo $sstate_suffixes

  # Figure out all the md5sums in the stamps dir.
  echo "Figuring out all the md5sums in stamps dir ... "
  for i in $sstate_suffixes; do
      # There is no "\.sigdata" but "_setcene" when it is mirrored
      # from the SSTATE_MIRRORS, use them to figure out the sum.
      sums=`find $stamps -maxdepth 3 -name "*.do_$i.*" \
        -o -name "*.do_${i}_setscene.*" | \
        sed -ne 's#.*_setscene\.##p' -e 's#.*\.sigdata\.##p' | \
        sed -e 's#\..*##' | sort -u`
      all_sums="$all_sums $sums"
  done
  echo "Done"

  echo "Figuring out the files which will be removed ... "
  for i in $all_sums; do
      grep ".*/sstate:[^:]*:[^:]*:[^:]*:[^:]*:[^:]*:[^:]*:${i}_.*" $cache_list >>$keep_list
  done
  echo "Done"

  if [ -s $keep_list ]; then
      sort -u $keep_list -o $keep_list
      to_del=`comm -1 -3 $keep_list $cache_list`
      gen_rmlist $rm_list "$to_del"
      let total_deleted=`cat $rm_list | sort -u | wc -w`
      if [ $total_deleted -gt 0 ]; then
          [ $debug -gt 0 ] && cat $rm_list | sort -u
          read_confirm
          if [ "$confirm" = "y" -o "$confirm" = "Y" ]; then
              echo "Removing sstate cache files ... ($total_deleted files)"
              # Remove them one by one to avoid the argument list too long error
              for i in `cat $rm_list | sort -u`; do
                  rm -f $verbose $i
              done
              echo "$total_deleted files have been removed"
          else
              do_nothing
          fi
      else
          no_files
      fi
  else
      echo_error "All files in cache dir will be removed! Abort!"
  fi

  rm -f $cache_list
  rm -f $keep_list
  rm -f $rm_list
}

# Parse arguments
while [ -n "$1" ]; do
  case $1 in
    --cache-dir=*)
      cache_dir=`echo $1 | sed -e 's#^--cache-dir=##' | xargs readlink -e`
      [ -d "$cache_dir" ] || echo_error "Invalid argument to --cache-dir"
      shift
        ;;
    --remove-duplicated|-d)
      rm_duplicated="y"
      shift
        ;;
    --yes|-y)
      confirm="y"
      shift
        ;;
    --follow-symlink|-L)
      fsym="y"
      shift
        ;;
    --extra-archs=*)
      extra_archs=`echo $1 | sed -e 's#^--extra-archs=##' -e 's#,# #g'`
      [ -n "$extra_archs" ] || echo_error "Invalid extra arch parameter"
      shift
        ;;
    --extra-layer=*)
      extra_layers=`echo $1 | sed -e 's#^--extra-layer=##' -e 's#,# #g'`
      [ -n "$extra_layers" ] || echo_error "Invalid extra layer parameter"
      for i in $extra_layers; do
          l=`readlink -e $i`
          if [ -d "$l" ]; then
              layers="$layers $l"
          else
              echo_error "Can't find layer $i"
          fi
      done
      shift
        ;;
    --stamps-dir=*)
      stamps=`echo $1 | sed -e 's#^--stamps-dir=##' -e 's#,# #g'`
      [ -n "$stamps" ] || echo_error "Invalid stamps dir $i"
      for i in $stamps; do
          [ -d "$i" ] || echo_error "Invalid stamps dir $i"
      done
      shift
        ;;
    --verbose|-v)
      verbose="-v"
      shift
        ;;
    --debug|-D)
      debug=`expr $debug + 1`
      echo "Debug level $debug"
      shift
        ;;
    --help|-h)
      usage
      exit 0
        ;;
    *)
      echo "Invalid arguments $*"
      echo_error "Try 'sstate-cache-management.sh -h' for more information."
        ;;
  esac
done

# sstate cache directory, use environment variable SSTATE_CACHE_DIR
# if it was not specified, otherwise, error.
[ -n "$cache_dir" ] || cache_dir=$SSTATE_CACHE_DIR
[ -n "$cache_dir" ] || echo_error "No cache dir found!"
[ -d "$cache_dir" ] || echo_error "Invalid cache directory \"$cache_dir\""

[ -n "$rm_duplicated" -a -n "$stamps" ] && \
    echo_error "Can not use both --remove-duplicated and --stamps-dir"

[ "$rm_duplicated" = "y" ] && remove_duplicated
[ -n "$stamps" ] && rm_by_stamps
[ -z "$rm_duplicated" -a -z "$stamps" ] && \
    echo "What do you want to do?"
exit 0
