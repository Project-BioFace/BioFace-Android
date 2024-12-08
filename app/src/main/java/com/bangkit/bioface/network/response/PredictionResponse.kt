package com.bangkit.bioface.network.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BaseResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String? = null
) : Serializable

data class HistoryResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("predictions")
    val predictions: List<PredictionHistory> // Ubah menjadi List<PredictionHistory>
) : Serializable

data class PredictionHistoryResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("prediction")
    val prediction: PredictionHistory // Ini adalah objek PredictionHistory
) : Serializable


data class PredictionHistory(
    @SerializedName("disease_accuracy")
    val diseaseAccuracy: String,

    @SerializedName("disease_description")
    val diseaseDescription: String,

    @SerializedName("face_disease")
    val faceDisease: String,

    @SerializedName("id")
    val id: Int,

    @SerializedName("image_url")
    val imageUrl: String,

    @SerializedName("prediction_detail")
    val predictionDetail: PredictionDetailHistory,

    @SerializedName("recomendation")
    val recomendation: Recommendation,

    @SerializedName("timestamp")
    val timestamp: String
) : Serializable

data class PredictionDetailHistory(
    @SerializedName("causes")
    var causes: List<String>? = null,

    @SerializedName("detail_disease_accuracy")
    var detailDiseaseAccuracy: String? = null
) : Serializable

data class PredictionResponse(
    @SerializedName("disease_accuracy")
    var diseaseAccuracy: String? = null,

    @SerializedName("disease_description")
    var diseaseDescription: String? = null,

    @SerializedName("face_disease")
    var faceDisease: String? = null,

    @SerializedName("image_url")
    var imageUrl: String? = null,

    @SerializedName("prediction_detail")
    var predictionDetail: PredictionDetail? = null,

    @SerializedName("recomendation")
    var recomendation: Recommendation? = null,

    @SerializedName("status")
    var status: String? = null
) : Serializable

data class PredictionDetail(
    @SerializedName("causes")
    var causes: List<String>? = null,

    @SerializedName("detail_disease_accuracy")
    var detailDiseaseAccuracy: Map<String, String>? = null // Untuk hasil prediksi
) : Serializable



// Model untuk Recommendation
data class Recommendation(
    @SerializedName("herbalSolutions")
    var herbalSolutions: List<HerbalSolution>? = null,

    @SerializedName("skincareProducts")
    var skincareProducts: List<SkincareProduct>? = null
) : Serializable

// Model untuk HerbalSolution
data class HerbalSolution(
    @SerializedName("name")
    var name: String? = null,

    @SerializedName("benefit")
    var benefit: String? = null,

    @SerializedName("usage")
    var usage: String? = null,

    @SerializedName("imageUrl")
    var imageUrl: String? = null
) : Serializable

// Model untuk SkincareProduct
data class SkincareProduct(
    @SerializedName("name")
    var name: String? = null,

    @SerializedName("imageUrl")
    var imageUrl: String? = null
) : Serializable
