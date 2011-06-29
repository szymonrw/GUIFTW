1.  Nested properties

    ```clj
    :layout.size.width 300
    -> (.. obj getLayout getSize (setWidth 300))
    ```
