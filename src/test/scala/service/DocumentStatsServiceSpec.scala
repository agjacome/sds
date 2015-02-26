package es.uvigo.ei.sing.sds.service

import scala.concurrent.duration._
import akka.actor.{ PoisonPill, Props }

import play.api.test.WithApplication

import es.uvigo.ei.sing.sds.ActorBaseSpec
import es.uvigo.ei.sing.sds.entity._
import es.uvigo.ei.sing.sds.database.DatabaseProfile

class DocumentStatsServiceSpec extends ActorBaseSpec with DocumentStatsServiceSpecExpectations {

  "The DocumentStats service" - {

    forAll(expectations) { (name, documents, keywords, annotations, stats) =>

      s"should correctly compute Document statistics for '$name'" in new WithApplication {
        val docStats = system.actorOf(Props[DocumentStatsService], "DocumentStatsComputer")
        val database = DatabaseProfile()
        implicit val session = database.createSession()

        import database._
        import database.profile.simple._

        Documents   ++= documents
        Keywords    ++= keywords
        Annotations ++= annotations

        docStats ! ComputeStats
        expectNoMsg()
        docStats ! PoisonPill

        DocumentStats.list should contain theSameElementsAs stats

        session.close()
      }

      s"should not modify any other database table aside from DocumentStats for '$name'" in new WithApplication {
        val docStats = system.actorOf(Props[DocumentStatsService], "DocumentStatsComputer")
        val database = DatabaseProfile()
        implicit val session = database.createSession()

        import database._
        import database.profile.simple._

        Documents   ++= documents
        Keywords    ++= keywords
        Annotations ++= annotations

        docStats ! ComputeStats
        expectNoMsg()
        docStats ! PoisonPill

        Documents.list   should contain theSameElementsAs documents
        Keywords.list    should contain theSameElementsAs keywords
        Annotations.list should contain theSameElementsAs annotations

        session.close()
      }

    }

  }

}

private[service] trait DocumentStatsServiceSpecExpectations extends ActorBaseSpec {

  protected type Stats = (DocumentId, KeywordId, Size, Double)

  protected lazy val expectations = Table(
    ("name", "documents", "keywords", "annotations", "stats"),
    ("Empty Set", Seq.empty[Document], Seq.empty[Keyword], Seq.empty[Annotation], Seq.empty[Stats]),
    (
      "Huge Test Set",
      Seq(
        Document(Some( 1), "[The therapeutic efficacy of cycloferon and the pharmacological activity of interferon inducers]."                                 , "irrelevant to this test", Some(24754075), true, false),
        Document(Some( 2), "An 'Upp'-turn in bacteriocin receptor identification."                                                                             , "irrelevant to this test", Some(24811684), true, false),
        Document(Some( 3), "Expression and purification of cyto-insectotoxin (Cit1a) using silkworm larvae targeting for an antimicrobial therapeutic agent."  , "irrelevant to this test", Some(24728600), true, false),
        Document(Some( 4), "Cationic Bioactive Peptide from the Seeds of Benincasa hispida."                                                                   , "irrelevant to this test", Some(24834076), true, false),
        Document(Some( 5), "Colonization and Infection of the Skin by S. aureus: Immune System Evasion and the Response to Cationic Antimicrobial Peptides."   , "irrelevant to this test", Some(24840573), true, false),
        Document(Some( 6), "Hydrophobicity and Helicity Regulate Antifungal Activity of 14-Helical β-Peptides."                                                , "irrelevant to this test", Some(24837702), true, false),
        Document(Some( 7), "Therapeutic potential of adenovirus-mediated delivery of β-defensin 2 for experimental otitis media."                              , "irrelevant to this test", Some(24842664), true, false),
        Document(Some( 8), "Effects of residue 5-point mutation and N-terminus hydrophobic residues on temporin-SHc physicochemical and biological properties.", "irrelevant to this test", Some(24842084), true, false),
        Document(Some( 9), "LL-37-derived peptides eradicate multidrug-resistant Staphylococcus aureus from thermally wounded human skin equivalents."         , "irrelevant to this test", Some(24841266), true, false),
        Document(Some(10), "Membrane curvature modulation of protein activity determined by NMR."                                                              , "irrelevant to this test", Some(24835017), true, false),
        Document(Some(11), "Genetic basis for Mycobacterium avium hominissuis resistance to host antimicrobial peptides."                                      , "irrelevant to this test", Some(24836414), true, false),
        Document(Some(12), "Forkhead, a new cross regulator of metabolism and innate immunity downstream of TOR in Drosophila."                                , "irrelevant to this test", Some(24842780), true, false),
        Document(Some(13), "Additivity and Synergy Between an Antimicrobial Peptide and Inhibitory Ions."                                                      , "irrelevant to this test", Some(24841756), true, false)
      ),
      Seq(
        Keyword(Some( 1), "interferon"                                                                                                                                                                                                                                                                                                                                                                                               , Protein ,  1),
        Keyword(Some( 2), "metallopeptidase"                                                                                                                                                                                                                                                                                                                                                                                         , Protein ,  1),
        Keyword(Some( 3), "cyto-insectotoxin"                                                                                                                                                                                                                                                                                                                                                                                        , Protein ,  1),
        Keyword(Some( 4), "egfp-cit1"                                                                                                                                                                                                                                                                                                                                                                                                , Protein ,  1),
        Keyword(Some( 5), "lipid ii and subunits"                                                                                                                                                                                                                                                                                                                                                                                    , Protein ,  1),
        Keyword(Some( 6), "bacteriocins"                                                                                                                                                                                                                                                                                                                                                                                             , Protein ,  1),
        Keyword(Some( 7), "synthetic gene"                                                                                                                                                                                                                                                                                                                                                                                           , DNA     ,  1),
        Keyword(Some( 8), "amps"                                                                                                                                                                                                                                                                                                                                                                                                     , Protein ,  6),
        Keyword(Some( 9), "uppp"                                                                                                                                                                                                                                                                                                                                                                                                     , Protein ,  1),
        Keyword(Some(10), "bacteriocin receptors"                                                                                                                                                                                                                                                                                                                                                                                    , Protein ,  1),
        Keyword(Some(11), "purified protein"                                                                                                                                                                                                                                                                                                                                                                                         , Protein ,  1),
        Keyword(Some(12), "InChI=1/C3H7NO2S/c4-2(1-7)3(5)6/h2,7H,1,4H2,(H,5,6)/f/h5H"                                                                                                                                                                                                                                                                                                                                                , Compound,  1),
        Keyword(Some(13), "InChI=1/C6H12O6/c7-1-3(9)5(11)6(12)4(10)2-8/h1,3-6,8-12H,2H2/t3-,4-,5-,6-/m1/s1"                                                                                                                                                                                                                                                                                                                          , Compound,  1),
        Keyword(Some(14), "InChI=1/C12H22O11/c13-1-3-5(15)6(16)9(19)12(22-3)23-10-4(2-14)21-11(20)8(18)7(10)17/h3-20H,1-2H2/t3-,4-,5-,6+,7-,8-,9-,10-,11?,12-/m1/s1"                                                                                                                                                                                                                                                                 , Compound,  1),
        Keyword(Some(15), "Lachesana tarabaevi"                                                                                                                                                                                                                                                                                                                                                                                      , Species ,  1),
        Keyword(Some(16), "Bombyx mori nucleopolyhedrovirus"                                                                                                                                                                                                                                                                                                                                                                         , Species ,  2),
        Keyword(Some(17), "Bombyx mori"                                                                                                                                                                                                                                                                                                                                                                                              , Species ,  3),
        Keyword(Some(18), "s. aureus virulence factors"                                                                                                                                                                                                                                                                                                                                                                              , Protein ,  1),
        Keyword(Some(19), "InChI=1/C17H20N2O6S/c1-17(2)12(16(22)23)19-14(21)11(15(19)26-17)18-13(20)10-8(24-3)6-5-7-9(10)25-4/h5-7,11-12,15H,1-4H3,(H,18,20)(H,22,23)/t11-,12+,15-/m1/s1/f/h18,22H"                                                                                                                                                                                                                                  , Compound,  2),
        Keyword(Some(20), "protein kinase c"                                                                                                                                                                                                                                                                                                                                                                                         , Protein ,  1),
        Keyword(Some(21), "camps"                                                                                                                                                                                                                                                                                                                                                                                                    , Protein ,  4),
        Keyword(Some(22), "il-8"                                                                                                                                                                                                                                                                                                                                                                                                     , Protein ,  1),
        Keyword(Some(23), "mrsa luh14616"                                                                                                                                                                                                                                                                                                                                                                                            , Protein ,  1),
        Keyword(Some(24), "p10"                                                                                                                                                                                                                                                                                                                                                                                                      , Protein ,  5),
        Keyword(Some(25), "naf"                                                                                                                                                                                                                                                                                                                                                                                                      , Protein ,  1),
        Keyword(Some(26), "12"                                                                                                                                                                                                                                                                                                                                                                                                       , Protein ,  1),
        Keyword(Some(27), "human cathelicidin ll-37"                                                                                                                                                                                                                                                                                                                                                                                 , Protein ,  1),
        Keyword(Some(28), "arva"                                                                                                                                                                                                                                                                                                                                                                                                     , Protein ,  1),
        Keyword(Some(29), "ll-37"                                                                                                                                                                                                                                                                                                                                                                                                    , Protein ,  3),
        Keyword(Some(30), "InChI=1/C10H16N2O8/c13-7(14)3-11(4-8(15)16)1-2-12(5-9(17)18)6-10(19)20/h1-6H2,(H,13,14)(H,15,16)(H,17,18)(H,19,20)/f/h13,15,17,19H"                                                                                                                                                                                                                                                                       , Compound,  3),
        Keyword(Some(31), "mupirocin-resistant mrsa luh15051"                                                                                                                                                                                                                                                                                                                                                                        , Protein ,  1),
        Keyword(Some(32), "InChI=1/FH.Na/h1H;/q;+1/p-1/fF.Na/h1h;/q-1;m"                                                                                                                                                                                                                                                                                                                                                             , Compound,  2),
        Keyword(Some(33), "mtk"                                                                                                                                                                                                                                                                                                                                                                                                      , Protein ,  3),
        Keyword(Some(34), "fkh"                                                                                                                                                                                                                                                                                                                                                                                                      , Protein ,  3),
        Keyword(Some(35), "tor regulators tsc1"                                                                                                                                                                                                                                                                                                                                                                                      , Protein ,  1),
        Keyword(Some(36), "InChI=1/C10H14N5O7P/c11-8-5-9(13-2-12-8)15(3-14-5)10-7(17)6(16)4(22-10)1-21-23(18,19)20/h2-4,6-7,10,16-17H,1H2,(H2,11,12,13)(H2,18,19,20)/t4-,6-,7-,10-/m1/s1/f/h18-19H,11H2"                                                                                                                                                                                                                             , Compound,  2),
        Keyword(Some(37), "insulin"                                                                                                                                                                                                                                                                                                                                                                                                  , Protein ,  1),
        Keyword(Some(38), "InChI=1/C51H79NO13/c1-30-16-12-11-13-17-31(2)42(61-8)28-38-21-19-36(7)51(60,65-38)48(57)49(58)52-23-15-14-18-39(52)50(59)64-43(33(4)26-37-20-22-40(53)44(27-37)62-9)29-41(54)32(3)25-35(6)46(56)47(63-10)45(55)34(5)24-30/h11-13,16-17,25,30,32-34,36-40,42-44,46-47,53,56,60H,14-15,18-24,26-29H2,1-10H3/b13-11+,16-12+,31-17+,35-25+/t30-,32-,33+,34-,36-,37+,38+,39+,40-,42+,43+,44-,46-,47+,51-/m1/s1", Compound,  2),
        Keyword(Some(39), "dfoxo"                                                                                                                                                                                                                                                                                                                                                                                                    , Protein ,  1),
        Keyword(Some(40), "toll"                                                                                                                                                                                                                                                                                                                                                                                                     , Protein ,  1),
        Keyword(Some(41), "InChI=1/3K.H3O4P/c;;;1-5(2,3)4/h;;;(H3,1,2,3,4)/q3*+1;/p-3/f3K.O4P/q3m;-3"                                                                                                                                                                                                                                                                                                                                , Compound,  1),
        Keyword(Some(42), "tor"                                                                                                                                                                                                                                                                                                                                                                                                      , Protein ,  2),
        Keyword(Some(43), "inactivated genes"                                                                                                                                                                                                                                                                                                                                                                                        , DNA     ,  1),
        Keyword(Some(44), "transcription factor"                                                                                                                                                                                                                                                                                                                                                                                     , Protein ,  1),
        Keyword(Some(45), "enter macrophages"                                                                                                                                                                                                                                                                                                                                                                                        , CellType,  1),
        Keyword(Some(46), "dpt"                                                                                                                                                                                                                                                                                                                                                                                                      , Protein ,  3),
        Keyword(Some(47), "macrophages"                                                                                                                                                                                                                                                                                                                                                                                              , CellType,  1),
        Keyword(Some(48), "tsc2"                                                                                                                                                                                                                                                                                                                                                                                                     , DNA     ,  1),
        Keyword(Some(49), "InChI=1/C12H26O4S.Na/c1-2-3-4-5-6-7-8-9-10-11-12-16-17(13,14)15;/h2-12H2,1H3,(H,13,14,15);/q;+1/p-1/fC12H25O4S.Na/q-1;m"                                                                                                                                                                                                                                                                                  , Compound,  1),
        Keyword(Some(50), "foxo family"                                                                                                                                                                                                                                                                                                                                                                                              , Protein ,  1),
        Keyword(Some(51), "maldi-tof"                                                                                                                                                                                                                                                                                                                                                                                                , Protein ,  1),
        Keyword(Some(52), "dfoxo null mutants"                                                                                                                                                                                                                                                                                                                                                                                       , Protein ,  1),
        Keyword(Some(53), "transcription factor dfoxo"                                                                                                                                                                                                                                                                                                                                                                               , Protein ,  1),
        Keyword(Some(54), "Candida albicans"                                                                                                                                                                                                                                                                                                                                                                                         , Species ,  6),
        Keyword(Some(55), "Homo sapiens"                                                                                                                                                                                                                                                                                                                                                                                             , Species ,  9),
        Keyword(Some(56), "Mycobacterium avium"                                                                                                                                                                                                                                                                                                                                                                                      , Species ,  5),
        Keyword(Some(57), "Staphylococcus aureus"                                                                                                                                                                                                                                                                                                                                                                                    , Species , 15),
        Keyword(Some(58), "Staphylococcus aureus"                                                                                                                                                                                                                                                                                                                                                                                    , Species ,  1),
        Keyword(Some(59), "Pelophylax saharicus"                                                                                                                                                                                                                                                                                                                                                                                     , Species ,  1),
        Keyword(Some(60), "Benincasa hispida"                                                                                                                                                                                                                                                                                                                                                                                        , Species ,  2),
        Keyword(Some(61), "Pseudomonas aeruginosa"                                                                                                                                                                                                                                                                                                                                                                                   , Species ,  1),
        Keyword(Some(62), "Saccharomyces cerevisiae"                                                                                                                                                                                                                                                                                                                                                                                 , Species ,  2),
        Keyword(Some(63), "Mus musculus"                                                                                                                                                                                                                                                                                                                                                                                             , Species ,  1),
        Keyword(Some(64), "Haemophilus influenzae"                                                                                                                                                                                                                                                                                                                                                                                   , Species ,  4),
        Keyword(Some(65), "Escherichia coli"                                                                                                                                                                                                                                                                                                                                                                                         , Species ,  2),
        Keyword(Some(66), "Enterococcus faecalis"                                                                                                                                                                                                                                                                                                                                                                                    , Species ,  1),
        Keyword(Some(67), "Candida parapsilosis"                                                                                                                                                                                                                                                                                                                                                                                     , Species ,  1)
      ),
      Seq(
        Annotation(Some(  1),  1,  1, "interferon"                        ,   32,   42),
        Annotation(Some(  2),  3,  3, "Cyto-insectotoxin"                 ,  144,  161),
        Annotation(Some(  3),  2,  2, "metallopeptidase"                  ,  899,  915),
        Annotation(Some(  4),  2,  5, "lipid II and subunits"             ,  557,  578),
        Annotation(Some(  5),  3,  4, "EGFP-Cit1"                         ,  689,  698),
        Annotation(Some(  6),  2,  6, "bacteriocins"                      ,  504,  516),
        Annotation(Some(  7),  3,  7, "synthetic gene"                    ,  288,  302),
        Annotation(Some(  8),  3,  8, "AMPs"                              ,   24,   28),
        Annotation(Some(  9),  2,  9, "UppP"                              , 1054, 1058),
        Annotation(Some( 10),  3, 11, "purified protein"                  ,  566,  582),
        Annotation(Some( 11),  2, 10, "bacteriocin receptors"             ,  382,  403),
        Annotation(Some( 12),  3, 12, "cysteine"                          ,  426,  434),
        Annotation(Some( 13),  2, 13, "mannose"                           ,  586,  593),
        Annotation(Some( 14),  2, 14, "maltose"                           ,  871,  878),
        Annotation(Some( 15),  3, 15, "Lachesana tarabaevi"               ,  263,  282),
        Annotation(Some( 16),  3, 16, "Bombyx mori nucleopolyhedrovirus"  ,  452,  484),
        Annotation(Some( 17),  3, 16, "BmNPV"                             ,  486,  491),
        Annotation(Some( 18),  3, 17, "silkworm"                          ,  509,  517),
        Annotation(Some( 19),  3, 17, "silkworm"                          , 1056, 1064),
        Annotation(Some( 20),  3, 17, "silkworms"                         , 1173, 1182),
        Annotation(Some( 21), 10, 20, "protein kinase C"                  ,  701,  717),
        Annotation(Some( 22),  5, 18, "S. aureus virulence factors"       ,  266,  293),
        Annotation(Some( 23),  9, 19, "methicillin"                       ,  168,  179),
        Annotation(Some( 24),  5, 19, "methicillin"                       ,  798,  809),
        Annotation(Some( 25),  5, 21, "CAMPs"                             ,  377,  382),
        Annotation(Some( 26),  6,  8, "AMPs"                              ,  146,  150),
        Annotation(Some( 27),  9, 22, "IL-8"                              , 1381, 1385),
        Annotation(Some( 28),  9, 23, "MRSA LUH14616"                     ,  764,  777),
        Annotation(Some( 29), 13, 21, "CAMPs"                             , 1551, 1556),
        Annotation(Some( 30), 13, 25, "NaF"                               ,  529,  532),
        Annotation(Some( 31),  9, 24, "P10"                               ,  670,  673),
        Annotation(Some( 32), 13, 26, "12"                                ,  314,  316),
        Annotation(Some( 33),  9, 27, "human cathelicidin LL-37"          ,  612,  636),
        Annotation(Some( 34), 13, 28, "ARVA"                              ,  357,  361),
        Annotation(Some( 35),  9, 29, "LL-37"                             ,  892,  897),
        Annotation(Some( 36), 13, 21, "CAMPs"                             ,  101,  106),
        Annotation(Some( 37),  9, 24, "P10"                               , 1488, 1491),
        Annotation(Some( 38), 13, 21, "CAMPs"                             , 1608, 1613),
        Annotation(Some( 39),  9, 24, "P10"                               , 1121, 1124),
        Annotation(Some( 40),  9, 24, "P10"                               , 1292, 1295),
        Annotation(Some( 41), 13, 30, "EDTA"                              ,  250,  254),
        Annotation(Some( 42),  9, 31, "mupirocin-resistant MRSA LUH15051" , 1179, 1212),
        Annotation(Some( 43), 13, 30, "EDTA"                              ,  822,  826),
        Annotation(Some( 44),  9, 29, "LL-37"                             , 1134, 1139),
        Annotation(Some( 45), 13, 32, "NaF"                               ,  529,  532),
        Annotation(Some( 46),  9, 29, "LL-37"                             ,  746,  751),
        Annotation(Some( 47), 13, 32, "NaF"                               , 1215, 1218),
        Annotation(Some( 48),  9, 24, "P10"                               ,  851,  854),
        Annotation(Some( 49), 13, 30, "EDTA"                              ,  994,  998),
        Annotation(Some( 50), 12, 33, "Mtk"                               , 1225, 1228),
        Annotation(Some( 51), 12, 34, "FKH"                               , 1058, 1061),
        Annotation(Some( 52), 12, 35, "TOR regulators TSC1"               ,  653,  672),
        Annotation(Some( 53), 12, 36, "AMP"                               ,  345,  348),
        Annotation(Some( 54), 12, 37, "insulin"                           , 1577, 1584),
        Annotation(Some( 55), 12, 38, "rapamycin"                         ,  442,  451),
        Annotation(Some( 56), 12, 36, "AMP"                               ,  524,  527),
        Annotation(Some( 57), 12, 39, "dFOXO"                             , 1519, 1524),
        Annotation(Some( 58), 12, 38, "rapamycin"                         ,  609,  618),
        Annotation(Some( 59), 12, 40, "Toll"                              ,  233,  237),
        Annotation(Some( 60),  4, 41, "potassium phosphate"               ,  228,  247),
        Annotation(Some( 61), 12, 42, "TOR"                               ,  453,  456),
        Annotation(Some( 62), 11, 43, "inactivated genes"                 ,  571,  588),
        Annotation(Some( 63), 12, 44, "transcription factor"              ,  968,  988),
        Annotation(Some( 64), 11, 45, "enter macrophages"                 ,  741,  758),
        Annotation(Some( 65), 12, 46, "Dpt"                               ,  736,  739),
        Annotation(Some( 66), 11, 47, "macrophages"                       ,  848,  859),
        Annotation(Some( 67), 12, 48, "TSC2"                              ,  673,  677),
        Annotation(Some( 68),  8, 49, "SDS"                               ,  360,  363),
        Annotation(Some( 69), 12, 50, "FoxO family"                       , 1032, 1043),
        Annotation(Some( 70), 12, 46, "Dpt"                               , 1217, 1220),
        Annotation(Some( 71),  4, 55, "human"                             ,  821,  826),
        Annotation(Some( 72), 11, 56, "M. avium"                          ,  253,  261),
        Annotation(Some( 73),  5, 55, "humans"                            ,  136,  142),
        Annotation(Some( 74),  9, 57, "S. aureus"                         ,  502,  511),
        Annotation(Some( 75),  7, 55, "human"                             ,  615,  620),
        Annotation(Some( 76),  4, 60, "B. hispida"                        , 1218, 1228),
        Annotation(Some( 77), 11, 56, "M. avium"                          ,  468,  476),
        Annotation(Some( 78),  6, 55, "human"                             ,  711,  716),
        Annotation(Some( 79),  5, 57, "S. aureus"                         ,  180,  189),
        Annotation(Some( 80),  9, 57, "MRSA"                              ,  543,  547),
        Annotation(Some( 81),  7, 55, "human"                             ,  781,  786),
        Annotation(Some( 82),  5, 57, "S. aureus"                         ,  266,  275),
        Annotation(Some( 83),  6, 54, "C. albicans"                       ,  931,  942),
        Annotation(Some( 84),  9, 55, "human"                             ,  612,  617),
        Annotation(Some( 85),  7, 55, "human"                             ,  826,  831),
        Annotation(Some( 86),  5, 57, "S. aureus"                         ,  507,  516),
        Annotation(Some( 87),  6, 54, "C. albicans"                       , 1108, 1119),
        Annotation(Some( 88),  9, 57, "MRSA"                              ,  764,  768),
        Annotation(Some( 89),  5, 57, "S. aureus"                         ,  565,  574),
        Annotation(Some( 90),  9, 55, "human"                             ,  986,  991),
        Annotation(Some( 91),  5, 57, "S. aureus"                         ,  784,  793),
        Annotation(Some( 92), 13, 61, "Pseudomonas aeruginosa"            ,  664,  686),
        Annotation(Some( 93),  9, 57, "MRSA"                              , 1157, 1161),
        Annotation(Some( 94),  5, 57, "Staphylococcus aureus"             ,  820,  841),
        Annotation(Some( 95),  8, 62, "yeasts"                            ,  214,  220),
        Annotation(Some( 96), 13, 57, "Staphylococcus aureus"             ,  709,  730),
        Annotation(Some( 97),  9, 57, "MRSA"                              , 1199, 1203),
        Annotation(Some( 98),  5, 57, "MRSA"                              ,  843,  847),
        Annotation(Some( 99),  9, 55, "human"                             , 1236, 1241),
        Annotation(Some(100),  5, 57, "S. aureus"                         , 1021, 1030),
        Annotation(Some(101),  9, 57, "MRSA"                              , 1427, 1431),
        Annotation(Some(102), 11, 63, "mice"                              , 1115, 1119),
        Annotation(Some(103),  5, 57, "S. aureus"                         , 1090, 1099),
        Annotation(Some(104), 11, 56, "M. avium"                          , 1166, 1174),
        Annotation(Some(105),  5, 57, "S. aureus"                         , 1306, 1315),
        Annotation(Some(106),  7, 64, "Haemophilus influenzae"            ,  873,  895),
        Annotation(Some(107),  7, 64, "NTHi"                              ,  897,  901),
        Annotation(Some(108),  7, 55, "human"                             ,  915,  920),
        Annotation(Some(109),  7, 64, "NTHi"                              , 1021, 1025),
        Annotation(Some(110),  7, 64, "NTHi"                              , 1207, 1211),
        Annotation(Some(111),  8, 65, "E. coli"                           , 1372, 1379),
        Annotation(Some(112), 13, 65, "E. coli"                           ,  768,  775),
        Annotation(Some(113), 13, 57, "S. aureus"                         ,  933,  942),
        Annotation(Some(114), 13, 54, "C. albicans"                       ,  947,  958),
        Annotation(Some(115), 13, 62, "yeast"                             , 1341, 1346),
        Annotation(Some(116), 13, 54, "C. albicans"                       , 1347, 1358),
        Annotation(Some(117),  8, 66, "E. faecalis"                       , 1381, 1392),
        Annotation(Some(118),  8, 67, "C. parapsilosis"                   , 1397, 1412)
      ),
      Seq(
        (DocumentId( 1), KeywordId( 1), Size( 1), 1.0),
        (DocumentId( 2), KeywordId(10), Size( 1), 1.0),
        (DocumentId( 2), KeywordId(13), Size( 1), 1.0),
        (DocumentId( 2), KeywordId(14), Size( 1), 1.0),
        (DocumentId( 2), KeywordId( 2), Size( 1), 1.0),
        (DocumentId( 2), KeywordId( 5), Size( 1), 1.0),
        (DocumentId( 2), KeywordId( 6), Size( 1), 1.0),
        (DocumentId( 2), KeywordId( 9), Size( 1), 1.0),
        (DocumentId( 3), KeywordId(11), Size( 1), 1.0),
        (DocumentId( 3), KeywordId(12), Size( 1), 1.0),
        (DocumentId( 3), KeywordId(15), Size( 1), 1.0),
        (DocumentId( 3), KeywordId(16), Size( 2), 1.0),
        (DocumentId( 3), KeywordId(17), Size( 3), 1.0),
        (DocumentId( 3), KeywordId( 3), Size( 1), 1.0),
        (DocumentId( 3), KeywordId( 4), Size( 1), 1.0),
        (DocumentId( 3), KeywordId( 7), Size( 1), 1.0),
        (DocumentId( 3), KeywordId( 8), Size( 1), 0.16666666666666666),
        (DocumentId( 4), KeywordId(41), Size( 1), 1.0),
        (DocumentId( 4), KeywordId(55), Size( 1), 0.1111111111111111),
        (DocumentId( 4), KeywordId(60), Size( 1), 0.5),
        (DocumentId( 5), KeywordId(18), Size( 1), 1.0),
        (DocumentId( 5), KeywordId(19), Size( 1), 0.5),
        (DocumentId( 5), KeywordId(21), Size( 1), 0.25),
        (DocumentId( 5), KeywordId(55), Size( 1), 0.1111111111111111),
        (DocumentId( 5), KeywordId(57), Size(10), 0.6666666666666666),
        (DocumentId( 6), KeywordId(54), Size( 2), 0.3333333333333333),
        (DocumentId( 6), KeywordId(55), Size( 1), 0.1111111111111111),
        (DocumentId( 6), KeywordId( 8), Size( 1), 0.16666666666666666),
        (DocumentId( 7), KeywordId(55), Size( 4), 0.4444444444444444),
        (DocumentId( 7), KeywordId(64), Size( 4), 1.0),
        (DocumentId( 8), KeywordId(49), Size( 1), 1.0),
        (DocumentId( 8), KeywordId(62), Size( 1), 0.5),
        (DocumentId( 8), KeywordId(65), Size( 1), 0.5),
        (DocumentId( 8), KeywordId(66), Size( 1), 1.0),
        (DocumentId( 8), KeywordId(67), Size( 1), 1.0),
        (DocumentId( 9), KeywordId(19), Size( 1), 0.5),
        (DocumentId( 9), KeywordId(22), Size( 1), 1.0),
        (DocumentId( 9), KeywordId(23), Size( 1), 1.0),
        (DocumentId( 9), KeywordId(24), Size( 5), 1.0),
        (DocumentId( 9), KeywordId(27), Size( 1), 1.0),
        (DocumentId( 9), KeywordId(29), Size( 3), 1.0),
        (DocumentId( 9), KeywordId(31), Size( 1), 1.0),
        (DocumentId( 9), KeywordId(55), Size( 3), 0.3333333333333333),
        (DocumentId( 9), KeywordId(57), Size( 6), 0.4),
        (DocumentId(10), KeywordId(20), Size( 1), 1.0),
        (DocumentId(11), KeywordId(43), Size( 1), 1.0),
        (DocumentId(11), KeywordId(45), Size( 1), 1.0),
        (DocumentId(11), KeywordId(47), Size( 1), 1.0),
        (DocumentId(11), KeywordId(56), Size( 3), 0.6),
        (DocumentId(11), KeywordId(63), Size( 1), 1.0),
        (DocumentId(12), KeywordId(33), Size( 1), 0.3333333333333333),
        (DocumentId(12), KeywordId(34), Size( 1), 0.3333333333333333),
        (DocumentId(12), KeywordId(35), Size( 1), 1.0),
        (DocumentId(12), KeywordId(36), Size( 2), 1.0),
        (DocumentId(12), KeywordId(37), Size( 1), 1.0),
        (DocumentId(12), KeywordId(38), Size( 2), 1.0),
        (DocumentId(12), KeywordId(39), Size( 1), 1.0),
        (DocumentId(12), KeywordId(40), Size( 1), 1.0),
        (DocumentId(12), KeywordId(42), Size( 1), 0.5),
        (DocumentId(12), KeywordId(44), Size( 1), 1.0),
        (DocumentId(12), KeywordId(46), Size( 2), 0.6666666666666666),
        (DocumentId(12), KeywordId(48), Size( 1), 1.0),
        (DocumentId(12), KeywordId(50), Size( 1), 1.0),
        (DocumentId(13), KeywordId(21), Size( 3), 0.75),
        (DocumentId(13), KeywordId(25), Size( 1), 1.0),
        (DocumentId(13), KeywordId(26), Size( 1), 1.0),
        (DocumentId(13), KeywordId(28), Size( 1), 1.0),
        (DocumentId(13), KeywordId(30), Size( 3), 1.0),
        (DocumentId(13), KeywordId(32), Size( 2), 1.0),
        (DocumentId(13), KeywordId(54), Size( 2), 0.3333333333333333),
        (DocumentId(13), KeywordId(57), Size( 2), 0.13333333333333333),
        (DocumentId(13), KeywordId(61), Size( 1), 1.0),
        (DocumentId(13), KeywordId(62), Size( 1), 0.5),
        (DocumentId(13), KeywordId(65), Size( 1), 0.5)
      )
    )
  )

}

