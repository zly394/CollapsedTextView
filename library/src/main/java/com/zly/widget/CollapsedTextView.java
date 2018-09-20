package com.zly.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import com.zly.utils.CharUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by zhuleiyue on 2017/3/12.
 */

public class CollapsedTextView extends AppCompatTextView implements View.OnClickListener {
    /**
     * 末尾省略号
     */
    private static final String ELLIPSE = "...";
    /**
     * 默认的折叠行数
     */
    public static final int COLLAPSED_LINES = 3;
    /**
     * 折叠时的默认文本
     */
    private static final String EXPANDED_TEXT = "展开";
    /**
     * 展开时的默认文本
     */
    private static final String COLLAPSED_TEXT = "收起";
    /**
     * 在文本末尾
     */
    public static final int END = 0;
    /**
     * 在文本下方
     */
    public static final int BOTTOM = 1;

    /**
     * 提示文字展示的位置
     */
    @IntDef({END, BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TipsGravityMode {
    }

    /**
     * 折叠的行数
     */
    private int mCollapsedLines;
    /**
     * 折叠时的文本
     */
    private String mExpandedText;
    /**
     * 展开时的文本
     */
    private String mCollapsedText;
    /**
     * 折叠时的图片资源
     */
    private Drawable mExpandedDrawable;
    /**
     * 展开时的图片资源
     */
    private Drawable mCollapsedDrawable;
    /**
     * 原始的文本
     */
    private CharSequence mOriginalText;
    /**
     * TextView中文字可显示的宽度
     */
    private int mShowWidth;
    /**
     * 是否是展开的
     */
    private boolean mIsExpanded;
    /**
     * 提示文字位置
     */
    private int mTipsGravity;
    /**
     * 提示文字颜色
     */
    private int mTipsColor;
    /**
     * 提示文字是否显示下划线
     */
    private boolean mTipsUnderline;
    /**
     * 提示是否可点击
     */
    private boolean mTipsClickable;
    /**
     * 提示文本的点击事件
     */
    private ExpandedClickableSpan mClickableSpan = new ExpandedClickableSpan();
    /**
     * TextView的点击事件监听
     */
    private OnClickListener mListener;
    /**
     * 是否响应TextView的点击事件
     */
    private boolean mIsResponseListener = true;

    public CollapsedTextView(Context context) {
        this(context, null);
    }

    public CollapsedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollapsedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        // 使点击有效
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * 初始化属性
     */
    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typed = context.obtainStyledAttributes(attrs, R.styleable.CollapsedTextView);
            mCollapsedLines = typed.getInt(R.styleable.CollapsedTextView_collapsedLines, COLLAPSED_LINES);
            setExpandedText(typed.getString(R.styleable.CollapsedTextView_expandedText));
            setCollapsedText(typed.getString(R.styleable.CollapsedTextView_collapsedText));
            setExpandedDrawable(typed.getDrawable(R.styleable.CollapsedTextView_expandedDrawable));
            setCollapsedDrawable(typed.getDrawable(R.styleable.CollapsedTextView_collapsedDrawable));
            mTipsGravity = typed.getInt(R.styleable.CollapsedTextView_tipsGravity, END);
            mTipsColor = typed.getColor(R.styleable.CollapsedTextView_tipsColor, 0);
            mTipsUnderline = typed.getBoolean(R.styleable.CollapsedTextView_tipsUnderline, false);
            mTipsClickable = typed.getBoolean(R.styleable.CollapsedTextView_tipsClickable, true);
            typed.recycle();
        }
    }

    /**
     * 设置折叠行数
     *
     * @param collapsedLines 折叠行数
     */
    public void setCollapsedLines(@IntRange(from = 0) int collapsedLines) {
        this.mCollapsedLines = collapsedLines;
    }

    /**
     * 设置折叠时的提示文本
     *
     * @param expandedText 提示文本
     */
    public void setExpandedText(String expandedText) {
        this.mExpandedText = TextUtils.isEmpty(expandedText) ? EXPANDED_TEXT : expandedText;
    }

    /**
     * 设置展开时的提示文本
     *
     * @param collapsedText 提示文本
     */
    public void setCollapsedText(String collapsedText) {
        this.mCollapsedText = collapsedText;
    }

    /**
     * 设置折叠时的提示图片
     *
     * @param resId 图片资源
     */
    public void setExpandedDrawableRes(@DrawableRes int resId) {
        setExpandedDrawable(ContextCompat.getDrawable(getContext(), resId));
    }

    /**
     * 设置折叠时的提示图片
     *
     * @param expandedDrawable 图片
     */
    public void setExpandedDrawable(Drawable expandedDrawable) {
        if (expandedDrawable != null) {
            this.mExpandedDrawable = expandedDrawable;
            this.mExpandedDrawable.setBounds(0, 0, mExpandedDrawable.getIntrinsicWidth(), mExpandedDrawable.getIntrinsicHeight());
        }
    }

    /**
     * 设置展开时的提示图片
     *
     * @param resId 图片资源
     */
    public void setCollapsedDrawableRes(@DrawableRes int resId) {
        setCollapsedDrawable(ContextCompat.getDrawable(getContext(), resId));
    }

    /**
     * 设置展开时的提示图片
     *
     * @param collapsedDrawable 图片
     */
    public void setCollapsedDrawable(Drawable collapsedDrawable) {
        if (collapsedDrawable != null) {
            this.mCollapsedDrawable = collapsedDrawable;
            this.mCollapsedDrawable.setBounds(0, 0, mCollapsedDrawable.getIntrinsicWidth(), mCollapsedDrawable.getIntrinsicHeight());
        }
    }

    /**
     * 设置提示的位置
     *
     * @param tipsGravity END 表示在文字末尾，BOTTOM 表示在文字下方
     */
    public void setTipsGravity(@TipsGravityMode int tipsGravity) {
        this.mTipsGravity = tipsGravity;
    }

    /**
     * 设置文字提示的颜色
     *
     * @param tipsColor 颜色
     */
    public void setTipsColor(@ColorInt int tipsColor) {
        this.mTipsColor = tipsColor;
    }

    /**
     * 设置提示文字是否有下划线
     *
     * @param tipsUnderline true 表示有下划线
     */
    public void setTipsUnderline(boolean tipsUnderline) {
        this.mTipsUnderline = tipsUnderline;
    }

    /**
     * 设置提示文字是否可点击
     *
     * @param tipsClickable true 表示可点击
     */
    public void setTipsClickable(boolean tipsClickable) {
        this.mTipsClickable = tipsClickable;
    }

    @Override
    public void setText(final CharSequence text, final BufferType type) {
        // 如果text为空或mCollapsedLines为0则直接显示
        if (TextUtils.isEmpty(text) || mCollapsedLines == 0) {
            super.setText(text, type);
        } else if (mIsExpanded) {
            // 保存原始文本，去掉文本末尾的空字符
            this.mOriginalText = CharUtil.trimFrom(text);
            formatExpandedText(type);
        } else {
            // 获取TextView中文字显示的宽度，需要在layout之后才能获取到，避免重复获取
            if (mShowWidth == 0) {
                getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        mShowWidth = getWidth() - getPaddingLeft() - getPaddingRight();
                        formatCollapsedText(type, text);
                    }
                });
            } else {
                formatCollapsedText(type, text);
            }
        }
    }

    /**
     * 格式化折叠时的文本
     *
     * @param type ref android.R.styleable#TextView_bufferType
     */
    private void formatCollapsedText(BufferType type, CharSequence text) {
        // 保存原始文本，去掉文本末尾的空字符
        this.mOriginalText = CharUtil.trimFrom(text);
        // 获取 layout，用于计算行数
        Layout layout = getLayout();
        // 调用 setText 用于重置 Layout
        if (layout == null || !layout.getText().equals(mOriginalText)) {
            super.setText(mOriginalText, type);
            layout = getLayout();
        }
        // 获取 paint，用于计算文字宽度
        TextPaint paint = getPaint();

        int line = layout.getLineCount();
        if (line <= mCollapsedLines) {
            super.setText(mOriginalText, type);
        } else {
            // 最后一行的开始字符位置
            int lastLineStart = layout.getLineStart(mCollapsedLines - 1);
            // 最后一行的结束字符位置
            int lastLineEnd = layout.getLineVisibleEnd(mCollapsedLines - 1);
            // 计算后缀的宽度
            int expandedTextWidth;
            if (mTipsGravity == END) {
                expandedTextWidth = (int) paint.measureText(ELLIPSE + " " + mExpandedText);
            } else {
                expandedTextWidth = (int) paint.measureText(ELLIPSE + " ");
            }
            // 最后一行的宽
            float lastLineWidth = layout.getLineWidth(mCollapsedLines - 1);
            // 如果大于屏幕宽度则需要减去部分字符
            if (lastLineWidth + expandedTextWidth > mShowWidth) {
                int cutCount = paint.breakText(mOriginalText, lastLineStart, lastLineEnd, false, expandedTextWidth, null);
                while (paint.measureText(mOriginalText.subSequence(lastLineStart, lastLineEnd - cutCount) + ELLIPSE + " " + mExpandedText) > mShowWidth) {
                    cutCount++;
                }
                lastLineEnd -= cutCount;
            }
            // 因设置的文本可能是带有样式的文本，如SpannableStringBuilder，所以根据计算的字符数从原始文本中截取
            SpannableStringBuilder spannable = new SpannableStringBuilder();
            // 截取文本，还是因为原始文本的样式原因不能直接使用paragraphs中的文本
            CharSequence ellipsizeText = mOriginalText.subSequence(0, lastLineEnd);
            spannable.append(ellipsizeText);
            spannable.append(ELLIPSE);
            // 设置样式
            setSpan(spannable);
            super.setText(spannable, type);
        }
    }

    /**
     * 格式化展开式的文本，直接在后面拼接即可
     *
     * @param type
     */
    private void formatExpandedText(BufferType type) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(mOriginalText);
        setSpan(spannable);
        super.setText(spannable, type);
    }

    /**
     * 设置提示的样式
     *
     * @param spannable 需修改样式的文本
     */
    private void setSpan(SpannableStringBuilder spannable) {
        Drawable drawable;
        // 根据提示文本需要展示的文字拼接不同的字符
        if (mTipsGravity == END) {
            spannable.append(" ");
        } else {
            spannable.append("\n");
        }
        int tipsLen;
        // 判断是展开还是收起
        if (mIsExpanded) {
            spannable.append(mCollapsedText);
            drawable = mCollapsedDrawable;
            tipsLen = mCollapsedText.length();
        } else {
            spannable.append(mExpandedText);
            drawable = mExpandedDrawable;
            tipsLen = mExpandedText.length();
        }
        // 设置点击事件
        spannable.setSpan(mClickableSpan, spannable.length() - tipsLen,
                spannable.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        // 如果提示的图片资源不为空，则使用图片代替提示文本
        if (drawable != null) {
            spannable.setSpan(new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE),
                    spannable.length() - tipsLen, spannable.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannable.append(" ");
        }
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        // 保存TextView的点击监听事件
        this.mListener = l;
        super.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (!mIsResponseListener) {
            // 如果不响应TextView的点击事件，将属性置为true
            mIsResponseListener = true;
        } else if (mListener != null) {
            // 如果响应TextView的点击事件且监听不为空，则响应点击事件
            mListener.onClick(v);
        }
    }

    /**
     * 提示的点击事件
     */
    private class ExpandedClickableSpan extends ClickableSpan {

        @Override
        public void onClick(View widget) {
            // 是否可点击
            if (mTipsClickable) {
                mIsResponseListener = false;
                mIsExpanded = !mIsExpanded;
                setText(mOriginalText);
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            // 设置提示文本的颜色和是否需要下划线
            ds.setColor(mTipsColor == 0 ? ds.linkColor : mTipsColor);
            ds.setUnderlineText(mTipsUnderline);
        }
    }
}
