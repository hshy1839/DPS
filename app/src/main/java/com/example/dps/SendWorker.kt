package com.example.dps

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.dps.mainActivity.MainActivity
import java.text.SimpleDateFormat
import java.util.Date

class SendWorker(context: Context, params:WorkerParameters): Worker(context, params) {
    companion object{
        const val KEY_WORKER = "key_worker"
    }
    // Worker에서 실시할 작업을 doWork() 안에서 정의
    override fun doWork(): Result {
        try{

                Log.d("UploadWorker", "background ....")

            val time = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentData = time.format(Date())

            // worker가 끝난 이후 반환할 데이터를 정의
            val outPutData = Data.Builder()
                .putString(KEY_WORKER, currentData)
                .build()

            // 데이터를 반환함과 동시에 success 처리
            return Result.success(outPutData)
        }catch (e:Exception){
            // worker 결과를 fail 처리
            return Result.failure()
        }
    }
}