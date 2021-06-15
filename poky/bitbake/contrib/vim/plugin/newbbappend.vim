" Vim plugin file
" Purpose:	Create a template for new bbappend file
" Author:	Joshua Watt <JPEWhacker@gmail.com>
" Copyright:	Copyright (C) 2017 Joshua Watt <JPEWhacker@gmail.com>
"
" This file is licensed under the MIT license, see COPYING.MIT in
" this source distribution for the terms.
"

if &compatible || v:version < 600 || exists("b:loaded_bitbake_plugin")
    finish
endif

fun! NewBBAppendTemplate()
    if line2byte(line('$') + 1) != -1
        return
    endif

    let l:paste = &paste
    set nopaste

    " New bbappend template
    0 put ='FILESEXTRAPATHS_prepend := \"${THISDIR}/${PN}:\"'
    2

    if paste == 1
        set paste
    endif
endfun

if !exists("g:bb_create_on_empty")
    let g:bb_create_on_empty = 1
endif

" disable in case of vimdiff
if v:progname =~ "vimdiff"
    let g:bb_create_on_empty = 0
endif

augroup NewBBAppend
    au BufNewFile,BufReadPost *.bbappend
                \ if g:bb_create_on_empty |
                \    call NewBBAppendTemplate() |
                \ endif
augroup END

