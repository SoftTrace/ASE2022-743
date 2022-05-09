package ir;

import algorithm.ALGO;
import document.SimilarityMatrix;
import document.TextDataset;
import experiment.Result;
import ir.model.IRModel;

public class IR {

    public static Result compute(TextDataset textDataset, IRModel irModel, ALGO algorithm) {
        Result result = null;
        try {
            SimilarityMatrix similarityMatrix = irModel.Compute(textDataset.getSourceCollection(),
                    textDataset.getTargetCollection());
            SimilarityMatrix matrix_improve = algorithm.improve(similarityMatrix, textDataset);
            result = new Result(matrix_improve, textDataset.getRtm());
            result.setAlgorithmName(algorithm.getAlgorithmName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.setModel(irModel.toString());
        return result;
    }

}
