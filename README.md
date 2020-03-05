# PostFix

## Repl Commands

Quit: `:q`

Debug On: `:d+`

Debug Off: `:d-`
  
## Postfix program

`(postfix [<value>*] (<command>*))`

Note: 

1) `<domain-variable>` see below for `value` and `command`.
2) `*` means zero or more than one

## Postfix value

Integer: `{0, 1, 2, ...}`

Executable Sequence: `(<command>*)`

## Postfix command

Integer: `{0, 1, 2, ...}`

Pop: `pop`

Swap: `swap`

Nget: `nget`

Sel: `sel`

Exec: `exec`

Arithmetic Operations: `add`, `sub`, `mul`, `div`, `rem`

Relational Operations: `lt`, `eq`, `gt`

Executable Sequence: `(<command>*)`

## Example

```
[info] running io.github.heyrutvik.postfix.Repl 
postfix> :d+
debug message turned on!
postfix> (postfix [4 5] ((2 (3 mul add) exec) 1 swap exec sub))
((2 (3 mul add) exec) 1 swap exec sub) [4 5]
(1 swap exec sub) [(2 (3 mul add) exec) 4 5]
(swap exec sub) [1 (2 (3 mul add) exec) 4 5]
(exec sub) [(2 (3 mul add) exec) 1 4 5]
(2 (3 mul add) exec sub) [1 4 5]
((3 mul add) exec sub) [2 1 4 5]
(exec sub) [(3 mul add) 2 1 4 5]
(3 mul add sub) [2 1 4 5]
(mul add sub) [3 2 1 4 5]
(add sub) [6 1 4 5]
(sub) [7 4 5]
() [-3 5]
value = -3
postfix> :d-
debug message turned off!
postfix> (postfix [4 5] ((2 (3 mul add) exec) 1 swap exec sub))
value = -3
postfix> [(2 add) 3]
stack = [(2 add) 3]
postfix> (2 (3 mul add) exec)
commands = (2 (3 mul add) exec)
postfix> :q
See you soon!
```