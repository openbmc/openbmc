
require systemtap_git.bb

inherit native

RM_WORK_EXCLUDE_ITEMS += "recipe-sysroot-native"
addtask addto_recipe_sysroot after do_populate_sysroot before do_build
