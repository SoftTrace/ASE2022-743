package ir;

import algorithm.ALGO;
import document.*;
import experiment.enums.LayerEnum;
import experiment.enums.ProjectEnum;
import experiment.Result;
import experiment.project.Project;
import ir.model.IRModel;
import ir.util.BitermUtil;
import ir.util.IRUtil;
import ir.util.WeightUtil;

import java.util.Collections;
import java.util.Map;
import java.util.Set;


public class IR_TAROT {
    private static ProjectEnum projectEnum;
    private static Boolean isUcType = false;


    public Result compute(Project project, TextDataset textDataset, IRModel irModel, ALGO algorithm,
                          boolean lambdaFlag, boolean thetaFlag) {
        projectEnum = ProjectEnum.getProject(project.getProjectName());
        if (projectEnum.equals(ProjectEnum.ITRUST) || projectEnum.equals(ProjectEnum.GANTT))
            isUcType = true;
        BitermUtil bitermUtil = new BitermUtil(project);
        SimilarityMatrix similarityMatrix;

        Map<String, Map<String, Integer>> reqBitermNumMap = bitermUtil.getReqBitermsMap();
        Map<String, Map<String, Integer>> codeBitermNumMap = bitermUtil.getCodeBitermsMap();
        Map<String, Map<LayerEnum, Set<String>>> reqLayerBitermMap = bitermUtil.getReqLayerBitermsMap();

        textDataset = bitermUtil.updateTextDataSet(textDataset, reqBitermNumMap, codeBitermNumMap);

        ArtifactsCollection reqCollection = textDataset.getSourceCollection();
        ArtifactsCollection clsCollection = textDataset.getTargetCollection();
        ArtifactsCollection reqCodeArtifacts = new ArtifactsCollection();
        reqCodeArtifacts.putAll(reqCollection);
        reqCodeArtifacts.putAll(clsCollection);
        TermDocumentMatrix reqTermMarix = new TermDocumentMatrix(reqCollection);
        TermDocumentMatrix clsTermMarix = new TermDocumentMatrix(clsCollection);
        TermDocumentMatrix reqCodeTermMarix = new TermDocumentMatrix(reqCodeArtifacts);

        TermDocumentMatrix TF = IRUtil.ComputeTF(reqCodeTermMarix);
        double[] IDF = IRUtil.ComputeIDF(IRUtil.ComputeDF(reqCodeTermMarix), reqCodeTermMarix.NumDocs());

        similarityMatrix = irModel.Compute(textDataset.getSourceCollection(), textDataset.getTargetCollection());

        SimilarityMatrix matrix_improve = algorithm.improve(similarityMatrix, textDataset);
        SimilarityMatrix tarot_matrix = new SimilarityMatrix();

        for (int i = 0; i < reqTermMarix.NumDocs(); i++) {
            LinksList links = new LinksList();
            String req = reqTermMarix.getDocumentName(i);
            for (int j = 0; j < clsTermMarix.NumDocs(); j++) {
                String cls = clsTermMarix.getDocumentName(j);
                double lambda = 0.0, theta = 0.0;
                if (lambdaFlag)
                    lambda = WeightUtil.getLambda(reqBitermNumMap.get(req).keySet(), codeBitermNumMap.get(cls).keySet(), TF, IDF);
                if (thetaFlag && lambda > 0)
                    theta = WeightUtil.getTheta(isUcType, reqLayerBitermMap.get(req), codeBitermNumMap.get(cls).keySet(), TF, IDF);

                double newScore = 0.0;
                if (lambda > 0) {
                    newScore = matrix_improve.getScoreForLink(req, cls) * (1 + lambda + theta);
                    if (newScore > 1) newScore = 0.999;
                } else {
                    newScore = matrix_improve.getScoreForLink(req, cls) * 0.9;
                }
                links.add(new SingleLink(req, cls, newScore));
            }
            links.sort(Collections.reverseOrder());
            for (SingleLink link : links) {
                tarot_matrix.addLink(link.getSourceArtifactId(), link.getTargetArtifactId(), link.getScore());
            }
        }

        Result result = new Result(tarot_matrix, textDataset.getRtm());
        result.setAlgorithmName(algorithm.getAlgorithmName());
        result.setModel(irModel.getModelName());

        return result;
    }
}
