meta-oe
=======

This layer depends on:

URI: git://github.com/openembedded/openembedded-core.git
branch: scarthgap 

luajit recipe requires host compiler to be able to generate 32bit code when target is 32bit
e.g. arm, so ensure that $CC -m32 is functional on build host, if building this recipe, needed
packages to fullfit this might have different names on different host distributions
e.g. on archlinux based distributions install prerequisites like below

pacman -S lib32-gcc-libs lib32-glibc

Ubuntu
sudo apt-get install gcc-multilib linux-libc-dev:i386

Send pull requests to openembedded-devel@lists.openembedded.org with '[meta-oe][scarthgap]' in the subject'

When sending single patches, please use something like:
'git send-email -M -1 --to openembedded-devel@lists.openembedded.org --subject-prefix="meta-oe][scarthgap][PATCH"'

You are encouraged to fork the mirror on GitHub https://github.com/openembedded/meta-openembedded
to share your patches, this is preferred for patch sets consisting of more than one patch.

Other services like GitLab, repo.or.cz or self-hosted setups are of course accepted as well,
'git fetch <remote>' works the same on all of them. We recommend GitHub because it is free, easy
to use, has been proven to be reliable and has a really good web GUI.

layer maintainer: Armin Kuster <akuster808@gmail.com>
