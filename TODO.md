1.  Add parent-style passing to gui-creator

    (in gui-creator-args-dispatch check for instance?
    guiftw.props.Property)

2.  Swing: Add *adder support (Issue #1)

    -  Factor out default adder to default-adder fn

3.  Add support for matching by class in class selectors.

4.  Swing: Create default style sheet

    - with specific adders for:

        1.  JTabbedPane -- invoke `addTab` instead `add` (+ the
            *tab-icon and *tab-title props -- think of a better names)

        2.  JScrolledPane -- invoke setViewportView (confirm the
            method)

     - any additional quirks?

5.  Consider using this "default" style sheet by default.

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
