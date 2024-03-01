#
# Lists of the ptest in meta-filesystems, sorted into two sets by the time they take
# Please keep these sorted in alphabetical order
#
# A first pass at getting all meta-filesystems recipes which inherit ptest
# meta_filesystems_ptest_recipes=$(bitbake-layers show-recipes --recipes-only --layer meta-filesystems --inherits ptest --bare | sed -e '1,/=== Matching recipes: ===/d')
# ptests which take less than ~30s each

PTESTS_FAST_META_FILESYSTEMS = "\
    e2tools \
    fuse3 \
"

PTESTS_SLOW_META_FILESYSTEMS = "\
"
PTESTS_PROBLEMS_META_FILESYSTEMS = "\
    sshfs-fuse \
"
