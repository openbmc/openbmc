
require systemtap_git.bb

inherit native

addtask addto_recipe_sysroot after do_populate_sysroot before do_build
