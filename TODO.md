5.  Consider using this guiftw.swing/swing-quirks style sheet by default.

    Add support for toolkit-defined default stylesheet. (in parse-gui
    probably).

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

8.  Consider caching computed styles for objects.

    It'll preserve style in a map in gui state so we could reuse it
    later. Needed for custom adders (we need to know *adder property
    of parent when creating children -- not possible atm).

9.  Make easy to write components ('gui-creator'-like fns)

10. Detect if child is a seq (at runtime, in gui-creator) and,
    eventually "add" all elements from it.
