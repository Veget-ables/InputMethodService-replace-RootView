package slpl.ac.myapplication

import android.graphics.Color
import android.inputmethodservice.InputMethodService
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout


class CustomKeyboard : InputMethodService() {
    private lateinit var mExtractEditLayout: View
    private lateinit var mExtractEditText: EditText
    private lateinit var mBaseFrameLayout: FrameLayout

    // 1. フルスクリーンモードでIMEを起動
    override fun onEvaluateFullscreenMode(): Boolean {
        return true
    }

    // 2. レイアウト変更のために，ExtractAreaの参照を取得
    override fun onCreateExtractTextView(): View {
        mExtractEditLayout = super.onCreateExtractTextView()
        mExtractEditText = mExtractEditLayout.findViewById(android.R.id.inputExtractEditText)
        mExtractEditText.setBackgroundColor(Color.RED)
        return mExtractEditLayout
    }

    // 3. レイアウトを再構築する
    override fun onCreateInputView(): View? {
        rebuildFullScreenView()
        initKeyViews()
        return null // 通常はKeyboardViewを返すが存在しないのでnullを返す
    }

    private fun rebuildFullScreenView() {
        // 4. デフォルトのレイアウトを変更してparentPanelをフラットな状態にする
        val parentPanel = mExtractEditLayout.parent.parent.parent as ViewGroup
        val fullScreenArea = parentPanel.getChildAt(0) as ViewGroup  // ExtractArea + CandidateArea
        fullScreenArea.removeAllViews()
        // parentPanelの子ViewにはinputAreaがいる
        // val inputArea = parentPanel.getChildAt(1)

        // 5. parentPanelの上にベースとなるFrameLayoutを置く
        val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        mBaseFrameLayout = FrameLayout(this)
        mBaseFrameLayout.layoutParams = params
        mBaseFrameLayout.setBackgroundColor(Color.DKGRAY)
        parentPanel.addView(mBaseFrameLayout)

        // 6. FrameLayoutにExtractEditTextを追加
        (mExtractEditLayout as ViewGroup).removeView(mExtractEditText)
        mExtractEditText.apply {
            inputType = InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
            gravity = Gravity.TOP
            val fp = FrameLayout.LayoutParams(100, 100)
            fp.gravity = Gravity.CENTER
            layoutParams = fp
        }
        mBaseFrameLayout.addView(mExtractEditText)
    }

    // 7. 文字入力機能を担う独自xmlをFrameLayoutに追加
    private fun initKeyViews() {
        val sampleLayout = layoutInflater.inflate(R.layout.custom_keys, null)
        mBaseFrameLayout.addView(sampleLayout)
        val button: Button = sampleLayout.findViewById(R.id.button)
        button.setOnClickListener {
            hideWindow() // 8. ウィンドウを閉じる
        }
    }
}
