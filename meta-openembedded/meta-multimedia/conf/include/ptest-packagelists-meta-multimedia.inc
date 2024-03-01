#
# Lists of the ptest in meta-multimedia, sorted into two sets by the time they take
# Please keep these sorted in alphabetical order
#
# A first pass at getting all meta-multimedia recipes which inherit ptest
# meta_multimedia_ptest_recipes=$(bitbake-layers show-recipes --recipes-only --layer meta-multimedia --inherits ptest --bare | sed -e '1,/=== Matching recipes: ===/d')
# ptests which take less than ~30s each

PTESTS_FAST_META_MULTIMEDIA = "\
"

PTESTS_SLOW_META_MULTIMEDIA = "\
"

PTESTS_PROBLEMS_META_MULTIMEDIA = "\
    libopenmpt \
"
