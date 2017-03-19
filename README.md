# CollapsedTextView

### 使用

#### Gradle

```
dependencies {
    compile 'com.zly.widget:collapsed-textview:1.0.2'
}
```

### 效果图

![](https://ww3.sinaimg.cn/large/006tNbRwgy1fdmk51rek3g30900g0ndq.gif)

```
<com.zly.widget.CollapsedTextView
    android:layout_width="match_parent" <!-- 不能使用wrap_content -->
    android:layout_height="wrap_content"
    android:layout_marginBottom="8dp"
    android:background="@android:color/white"
    android:text=Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World!"
    app:collapsedLines="4"
    app:expandedText="展开全文"
    app:collapsedText="收起全文"
    app:tipsColor="#FF0000"
    app:tipsUnderline="true"
    app:expandedDrawable="@mipmap/ic_expanded"
    app:collapsedDrawable="@mipmap/ic_collapsed"
    app:tipsGravity="bottom"
    app:tipsClickable="true" />

```

```
app:collapsedLines="4"                        // 折叠行数，默认为4行
app:expandedText="展开全文"                    // 折叠时的提示文案，默认为"展开全文"
app:collapsedText="收起全文"                   // 展开时的提示文案，默认为"收起全文"
app:tipsColor="#FF0000"                       // 提示文案的颜色，默认为linkColor
app:tipsUnderline="false"                     // 提示文案是否有下划线，默认没有
app:expandedDrawable="@mipmap/ic_expanded"    // 折叠时的提示图片，优先级大于文案
app:collapsedDrawable="@mipmap/ic_collapsed"  // 展开时的提示图片，优先级大于文案
app:tipsGravity="end"                         // 提示的位置，end(默认)--在文字末尾，bottom-在文字下面
app:tipsClickable="true"                      // 提示是否可点击，默认不可点击
```

### 更新说明

#### v1.0.2

* 修复了提示文案点击事件和TextView点击事件同时响应的BUG
* 修复了折叠时设置带有样式的文本显示异常的BUG