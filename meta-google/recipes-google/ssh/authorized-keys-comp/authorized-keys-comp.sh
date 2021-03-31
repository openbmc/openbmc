#!/bin/bash
shopt -s nullglob

# We want to iterate over all system users, check if they are opted-in to ssh
# authorized_keys building, and then construct their keyfile
for user in $(cut -d':' -f1 /etc/passwd); do
  home="$(eval echo ~$user)" || continue
  link="$(readlink $home/.ssh/authorized_keys 2>/dev/null)" || continue
  # Users are only opted-in if they symlink to our well-known directory where
  # the final output of this script lives.
  if [ "$link" != "/run/authorized_keys/$user" ]; then
    echo "Ignoring $user $home/.ssh/authorized_keys" >&2
    continue
  fi

  echo "Updating $link" >&2
  declare -A basemap=()
  declare -a dirs=(
    "/usr/share/authorized_keys.d/$user"
    "$home/.ssh/authorized_keys.d"
    "/run/authorized_keys.d/$user"
  )
  # Build a map that can be used for sorting directories by their priority
  # and prioritizing the last listed directories over the later ones. We
  # append a counter to ensure that there is a stable sorting mechanism for
  # duplicate filenames. Duplicate filenames will be overridden by higher
  # priority directories.
  # Ex.
  #   /usr/share/authorized_keys.d/root/10-key
  #   /usr/share/authorized_keys.d/root/15-key
  #   /run/authorized_keys.d/root/10-key
  #   /run/authorized_keys.d/root/20-key
  #  Becomes
  #   ["10-key"]="/run/authorized_keys.d/root/10-key"
  #   ["15-key"]="/usr/share/authorized_keys.d/root/15-key"
  #   ["20-key"]="/run/authorized_keys.d/root/20-key"
  for dir in "${dirs[@]}"; do
    for file in "$dir"/*; do
      basemap["${file##*/}"]="$file"
    done
  done
  rm -f /run/authorized_keys.tmp
  touch /run/authorized_keys.tmp
  for key in $(printf "%s\n" "${!basemap[@]}" | sort -r); do
    echo "  Including ${basemap[$key]}" >&2
    cat "${basemap[$key]}" >>/run/authorized_keys.tmp
  done
  mkdir -p /run/authorized_keys
  mv /run/authorized_keys.tmp /run/authorized_keys/$user
  chown $user /run/authorized_keys/$user
done
