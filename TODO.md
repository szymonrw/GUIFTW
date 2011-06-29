5.  Consider using this guiftw.swing/swing-quirks style sheet by default.

6.  Add function helpers for more convenient selecting objects at
    runtime. Like getting children of some object and selecting only
    those who belong to some group. (think of more)

7.  SWT: Add support for custom function running inside swt-loop to
    catch exceptions. Then user could write function like:

    ```clj
    (defn my-exception-handler [f]
      (try (f)
        (catch A a ...)
        (catch B b ...)
        (finally ...)))
    ```

    and invoke swt-loop like (swt-loop my-exception-handler)

    Alternative version: user writes only function that gets
    exceptions as a argument. (less likely, user have less control)

8.  TREE: Add support for composing gui structures of smaller ones. e.g. to
    write

    ```clj
    (let [a (swing [JButton])
          b (swing [JButton])]
     (swing [JFrame [] a b]))
    ```

9.  Consider caching computed styles for objects.

    It'll preserve style in a map in gui state so we could reuse it
    later. Needed for custom adders (we need to know *adder property
    of parent when creating children -- not possible atm).

10. Throw meaningful exception when class is not found instead of
    NullPointerException.

