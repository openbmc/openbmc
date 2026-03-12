sudo dnf install -y wget perl
textooldir=${TEXTOOLDIR:-"./docs-build-tex-tools"}
mkdir -p "$textooldir"/tl
wget https://mirror.ctan.org/systems/texlive/tlnet/install-tl-unx.tar.gz -O "$textooldir"/install-tl-unx.tar.gz
tar xzf "$textooldir"/install-tl-unx.tar.gz -C "$textooldir"
"$textooldir"/install-tl-*/install-tl --scheme=small --texdir="${textooldir}"/tl --no-interaction
PATH="$PATH:${textooldir}/tl/bin/x86_64-linux" tlmgr install titlesec varwidth tabulary needspace upquote framed capt-of wrapfig fncychap gnu-freefont ctex latexmk nimbus15
rm -r "$textooldir"/install-tl*
