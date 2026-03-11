This layer depends on:

URI: git://git.openembedded.org/openembedded-core
branch: master

URI: git://git.openembedded.org/meta-openembedded
layers: meta-oe
branch: master

Send pull requests to openembedded-devel@lists.openembedded.org with '[meta-multimedia]' in the subject

When sending single patches, please use something like:
git send-email -M -1 --to openembedded-devel@lists.openembedded.org --subject-prefix='meta-multimedia][PATCH'

You are encouraged to fork the mirror on github https://github.com/openembedded/meta-openembedded to share your patches, this is preferred for patch sets consisting of more than one patch. Other services like GitLab, repo.or.cz or self hosted setups are of course accepted as well, 'git fetch <remote>' works the same on all of them. We recommend github because it is free, easy to use, has been proven to be reliable and has a really good web GUI.

Layer maintainer: Andreas Müller <schnitzeltony@gmail.com>
