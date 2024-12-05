package com.bangkit.bioface.network.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable


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
) : java.io.Serializable


data class PredictionDetail(
    @SerializedName("causes")
    var causes: List<String>? = null,

    @SerializedName("detail_disease_accuracy")
    var detailDiseaseAccuracy: Map<String, String>? = null
) : java.io.Serializable

data class Recommendation(
    @SerializedName("herbalSolutions")
    var herbalSolutions: List<HerbalSolution>? = null,

    @SerializedName("skincareProducts")
    var skincareProducts: List<SkincareProduct>? = null
) : java.io.Serializable

data class HerbalSolution(
    @SerializedName("benefit")
    var benefit: String? = null,

    @SerializedName("imageUrl")
    var imageUrl: String? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("usage")
    var usage: String? = null
) : java.io.Serializable

data class SkincareProduct(
    @SerializedName("imageUrl")
    var imageUrl: String? = null,

    @SerializedName("name")
    var name: String? = null
) : Serializable
