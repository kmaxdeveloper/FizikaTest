package uz.kmax.fizikatest.presentation.ui.fragment.main

import android.graphics.Color
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.get
import androidx.core.view.size
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.fizikatest.R
import uz.kmax.fizikatest.data.manager.AdsManager
import uz.kmax.fizikatest.domain.models.BaseTestData
import uz.kmax.fizikatest.databinding.FragmentTestBinding
import uz.kmax.fizikatest.presentation.ui.view.MathView
import uz.kmax.fizikatest.presentation.ui.dialog.DialogBack
import uz.kmax.fizikatest.presentation.ui.dialog.DialogEndTest
import uz.kmax.fizikatest.data.firebase.FirebaseManager
import uz.kmax.fizikatest.data.manager.TestManager
import uz.kmax.fizikatest.data.tools.SharedPref
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class TestFragment(private var testLocation: String, private var testCount : Int) :
    BaseFragmentWC<FragmentTestBinding>(FragmentTestBinding::inflate) {
    
    @Inject
    lateinit var sharedPref: SharedPref

    @Inject
    lateinit var adsManager: AdsManager
    
    private var testManager: TestManager = TestManager()
    private val testLinearLayouts by lazy { ArrayList<LinearLayoutCompat>() }
    private val variantList by lazy { ArrayList<MathView>() }
    private lateinit var testStatus: ArrayList<AppCompatTextView>
    private lateinit var firebaseManager: FirebaseManager
    private var variantSelected = false
    private var testStatusCount = 0
    private var countTest = 0
    private var dialogEnd = DialogEndTest()
    private var dialogBack = DialogBack()
    private var language = "uz"

    override fun onViewCreated() {
        firebaseManager = FirebaseManager()
        language = sharedPref.getLanguage().toString()
        adsManager.loadBanners(binding.bannerAds)
        startTest(testLocation,testCount)
    }

    override fun onResume() {
        super.onResume()
        adsManager.loadBanners(binding.bannerAds)
    }

    private fun startTest(testLocation: String,testCount: Int) {
        val randomTest = random(testCount)
        firebaseManager.observeList("Test/$language/$testLocation/V$randomTest", BaseTestData::class.java){
            if (it != null){
                val listTest = ArrayList<BaseTestData>()
                listTest.addAll(it)
                listTest.shuffle()
                countTest = listTest.size
                testManager.setTestList(listTest)
                loadView()
                loadDataToView()
            }
        }
    }

    private fun variantStyleRestart(){
        for (i in 0 until 4){
            testLinearLayouts[i].setBackgroundResource(R.drawable.style_test_default_answer)
        }
    }
    private fun loadView() {
        testStatus = ArrayList()
        for (i in 0 until binding.testCountLayout.size) {
            if (i < countTest) {
                testStatus.add(binding.testCountLayout.getChildAt(i) as AppCompatTextView)
            }else{
                binding.testCountLayout.getChildAt(i).visibility = View.GONE
            }
        }
        for (i in 0 until binding.group.childCount) {
            if (binding.group.getChildAt(i) is LinearLayoutCompat) {
                testLinearLayouts.add(binding.group.getChildAt(i) as LinearLayoutCompat)
            }
        }

        variantStyleRestart()

        variantList.add(binding.variantA)
        variantList.add(binding.variantB)
        variantList.add(binding.variantC)
        variantList.add(binding.variantD)

        binding.testCountLayout[positionAnswer()].setBackgroundResource(R.drawable.style_position_answer)

        binding.back.setOnClickListener {
            dialogBack.show(requireContext())
            dialogBack.setOnBackYesListener {
                ads()
            }
        }
        binding.nextBtn.setOnClickListener {
            next()
        }
        binding.stopTest.setOnClickListener {
            dialogBack.show(requireContext())
            dialogBack.setOnBackYesListener {
                ads()
            }
        }
    }

    fun next() {
        if (variantSelected) {
            if (testManager.hasNextQuestion()) {
                loadDataToView()
                variantStyleRestart()
                binding.testCountLayout[positionAnswer()].setBackgroundResource(R.drawable.style_position_answer)
                variantSelected = false

                if (positionAnswer() == countTest){
                    binding.nextBtn.text = "Finish"
                    binding.nextBtn.textSize = 15f
                }
            } else {
                dialogEnd.show(
                    requireContext(),
                    testManager.correctAnswerCount,
                    testManager.wrongAnswerCount
                )
                dialogEnd.setOnOkBtnListener {
                    ads()
                }
                dialogEnd.setOnReStartListener {
                    testManager.currentQuestionPosition = 0
                    loadDataToView()
                    for (i in 0 until binding.testCountLayout.size) {
                        binding.testCountLayout.getChildAt(i)
                            .setBackgroundResource(R.drawable.style_test_count)
                    }
                    for (i in 0 until testLinearLayouts.size){
                        testLinearLayouts[i].setBackgroundResource(R.drawable.style_test_default_answer)
                    }
                    variantSelected = false
                    testStatusCount = 0
                    testManager.correctAnswerCount = 0
                    testManager.wrongAnswerCount = 0
                    binding.nextBtn.text = getText(R.string.next)
                }
            }
        } else {
            Snackbar.make(binding.nextBtn, "Variantni tanlang !", Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.BLUE)
                .setTextColor(Color.WHITE)
                .show()
        }
    }
    private fun loadDataToView() {
        binding.question.setLatex(testManager.getQuestion().replace("\\n","\n"))
        variantList[0].setLatex(testManager.getVariantA().replace("\\n","\n"))
        variantList[1].setLatex(testManager.getVariantB().replace("\\n","\n"))
        variantList[2].setLatex(testManager.getVariantC().replace("\\n","\n"))
        variantList[3].setLatex(testManager.getVariantD().replace("\\n","\n"))

        if (positionAnswer() == countTest-1){
            binding.nextBtn.text = "Finish"
            binding.nextBtn.textSize = 15f
        }

        for (i in 0 until 4){
            testLinearLayouts[i].setOnClickListener {
                if (!variantSelected){
                    variantSelected = true
                    check(i)
                }else{
                    Snackbar.make(binding.nextBtn, "Javob belgilangan keyingi savolga o'ting !", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(Color.GREEN)
                        .setTextColor(Color.WHITE)
                        .show()
                }
            }
        }
    }
    private fun check(position: Int){
        val selectedAnswer = variantList[position].rawText
        testManager.checkAnswer(selectedAnswer)
        if (testManager.checkAnswerBoolean(selectedAnswer)){
            testLinearLayouts[position].setBackgroundResource(R.drawable.style_test_correct_answer)
            binding.testCountLayout.getChildAt(testStatusCount)
                .setBackgroundResource(R.drawable.style_true_answer)
            countUp()
        }else{
            testLinearLayouts[position].setBackgroundResource(R.drawable.style_test_wrong_answer)
            binding.testCountLayout.getChildAt(testStatusCount)
                .setBackgroundResource(R.drawable.style_wrong_answer)
            countUp()
            for (i in 0 until 4){
                if (testManager.checkAnswerBoolean(variantList[i].rawText)){
                    testLinearLayouts[i].setBackgroundResource(R.drawable.style_test_correct_answer)
                }
            }
        }
    }
    private fun countUp() {
        if (testStatusCount == countTest || testStatusCount > countTest) {
            testStatusCount = 0
        } else {
            testStatusCount++
        }
    }
    private fun random(testCount: Int):Int{
        val untilRandom = testCount + 1
        val random = Random.nextInt(0,untilRandom)
        if (random == 0){
            return 1
        }else if (random == untilRandom){
            return random - 1
        }
        return random
    }
    private fun ads() {
        adsManager.setOnAdDismissListener {
            startMainFragment(MenuFragment())
        }
        adsManager.showAds(requireActivity(), isBackPress = true) { success ->
            if (!success) {
                startMainFragment(MenuFragment())
            }
        }
    }
    private fun positionAnswer():Int{
        if (testStatusCount == countTest){
            return countTest - 1
        }
        return testStatusCount
    }

    override fun onDestroyView() {
        adsManager.setOnAdDismissListener {}
        adsManager.setOnAdClickListener {}
        super.onDestroyView()
    }
}
