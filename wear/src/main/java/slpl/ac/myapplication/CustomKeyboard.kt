package slpl.ac.myapplication

import android.graphics.Color
import android.inputmethodservice.InputMethodService
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.view.Gravity
import android.widget.Button

class CustomKeyboard : InputMethodService() {
    private lateinit var mFullScreenExtractView: View
    private lateinit var mFullScreenExtractTextView: EditText
    private lateinit var mBaseFrameLayout: FrameLayout

    // フルスクリーンモードで起動
    override fun onEvaluateFullscreenMode(): Boolean {
        return true
    }

    // レイアウトを操作するために，Extractに関するView参照を取得
    override fun onCreateExtractTextView(): View {
        mFullScreenExtractView = super.onCreateExtractTextView()
        mFullScreenExtractTextView = mFullScreenExtractView.findViewById(android.R.id.inputExtractEditText)
        mFullScreenExtractTextView.setBackgroundColor(Color.RED)
        return mFullScreenExtractView
    }

    // フルスクリーンモードのレイアウトを再構築する
    override fun onCreateInputView(): View? {
        rebuildBaseView()
        initKeyViews()
        return null // 通常はkeyboardViewを返すが存在しないので，nullを返す
    }

    private fun rebuildBaseView(){
        // 標準搭載のレイアウトを削除してフラットな状態にし，新しくベースレイアウトに置き換える
        val parent = mFullScreenExtractView.parent.parent.parent as ViewGroup
        val showView = parent.getChildAt(0)  // EditText + ActionButton
        val inputView = parent.getChildAt(1) // KeyboardView
        parent.removeView(showView)
        parent.removeView(inputView)
        val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        mBaseFrameLayout = FrameLayout(this)
        mBaseFrameLayout.layoutParams = params
        mBaseFrameLayout.setBackgroundColor(Color.BLACK)
        parent.addView(mBaseFrameLayout)

        // baseLayoutにKeyboardViewを追加
        mBaseFrameLayout.addView(inputView, params)

        // baseLayoutにEditTextを追加
        (mFullScreenExtractView as ViewGroup).removeView(mFullScreenExtractTextView)
        mFullScreenExtractTextView.apply {
            inputType = InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
            gravity = Gravity.TOP
            val fp = FrameLayout.LayoutParams(100, 100)
            fp.gravity = Gravity.CENTER
            layoutParams = fp
        }
        mBaseFrameLayout.addView(mFullScreenExtractTextView)
    }

    private fun initKeyViews(){
        // KeyとなるViewをベースレイアウトに追加
        val sampleLayout = layoutInflater.inflate(R.layout.custom_keys, null)
        mBaseFrameLayout.addView(sampleLayout)
        val button : Button = sampleLayout.findViewById(R.id.button)
        button.setOnClickListener {
            hideWindow() // IMEを終了
        }
    }
}
