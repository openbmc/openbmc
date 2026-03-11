" Vim filetype detection file
" Language:     BitBake
" Author:       Ricardo Salveti <rsalveti@rsalveti.net>
" Copyright:    Copyright (C) 2008  Ricardo Salveti <rsalveti@rsalveti.net>
" Licence:      You may redistribute this under the same terms as Vim itself
"
" This sets up the syntax highlighting for BitBake files, like .bb, .bbclass and .inc

if &compatible || version < 600 || exists("b:loaded_bitbake_plugin")
    finish
endif

" .bb, .bbappend and .bbclass
au BufNewFile,BufRead *.{bb,bbappend,bbclass}  setfiletype bitbake

" .inc -- meanwhile included upstream
if !has("patch-9.0.0055")
    au BufNewFile,BufRead *.inc                call s:BBIncDetect()
    def s:BBIncDetect()
        l:lines = getline(1) .. getline(2) .. getline(3)
        if l:lines =~# '\<\%(require\|inherit\)\>' || lines =~# '[A-Z][A-Za-z0-9_:${}]*\s\+\%(??\|[?:+]\)\?= '
            set filetype bitbake
        endif
    enddef
endif

" .conf
au BufNewFile,BufRead *.conf
    \ if (match(expand("%:p:h"), "conf") > 0) |
    \     set filetype=bitbake |
    \ endif

