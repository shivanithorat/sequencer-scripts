package aps

import csw.params.commands.CommandResponse
import csw.params.commands.SequenceCommand
import csw.params.commands.Sequence
import csw.prefix.models.Subsystem
import esw.ocs.api.models.ObsMode
import esw.ocs.dsl.core.script
import esw.ocs.dsl.highlevel.models.ESW
import esw.ocs.dsl.highlevel.models.LGSF
import esw.ocs.dsl.highlevel.models.APS
import kotlin.time.milliseconds
import kotlin.time.seconds

script {
    println("Loaded aps test1")

   val obsMode1 = ObsMode("takeFrame")
    val obsMode2 = ObsMode("processFrame")
    val defaultTimeout = 5.seconds
    val frametakingSequencer = Sequencer(APS, obsMode1, defaultTimeout)
    val frameprocessingSequencer = Sequencer(APS, obsMode2, defaultTimeout)

    suspend fun executeSingleLoop() {
        val apsCommand1: SequenceCommand = Setup("APS.test", "command1")
        val apsCommand2: SequenceCommand = Setup("APS.test", "command2")
        val sequence1: Sequence = sequenceOf(apsCommand1, apsCommand2)
        val submitResponse1: CommandResponse.SubmitResponse = frametakingSequencer.submit(sequence1)
        println(submitResponse1)

        val apsCommand3: SequenceCommand = Setup("APS.test", "command1")
        val apsCommand4: SequenceCommand = Setup("APS.test", "command2")
        val sequence2: Sequence = sequenceOf(apsCommand3, apsCommand4)
        val submitResponse2: CommandResponse.SubmitResponse = frameprocessingSequencer.submit(sequence2)
        println(submitResponse2)

        // wait until both are completed before finishing
        val finalResponse1: CommandResponse.SubmitResponse = frametakingSequencer.queryFinal(submitResponse1.runId())
        println(finalResponse1)
        val finalResponse2: CommandResponse.SubmitResponse = frameprocessingSequencer.queryFinal(submitResponse2.runId())
        println(finalResponse2)
    }

    onSetup("test") {
        println("onSetup::test")

        executeSingleLoop()

    }







    // val testAssembly = Assembly(ESW, "test", defaultTimeout)

    /*
    onSetup("command-for-assembly") { command ->
        testAssembly.submit(command)
    }

    onSetup("command-4") {
        // try sending concrete sequence
        val setupCommand = Setup(
                "TCS.test",
                "command-3"
        )
        val sequence = sequenceOf(setupCommand)

        // ESW-88, ESW-145, ESW-195
        val tcsSequencer = Sequencer(TCS, "darknight", defaultTimeout)
        tcsSequencer.submitAndWait(sequence, 10.milliseconds)
    }

    onSetup("command-lgsf") {
        // NOT update command response to avoid sequencer to finish immediately
        // so that other Add, Append command gets time
        val setupCommand = Setup("LGSF.test", "command-lgsf")
        lgsfSequencer.submitAndWait(sequenceOf(setupCommand), 10.milliseconds)
    }
    */
    onDiagnosticMode { startTime, hint ->
        // do some actions to go to diagnostic mode based on hint
        //testAssembly.diagnosticMode(startTime, hint)
    }

    onOperationsMode {
        // do some actions to go to operations mode
        //testAssembly.operationsMode()
    }

    onGoOffline {
        // do some actions to go offline
        //testAssembly.goOffline()
    }

    onGoOnline {
        // do some actions to go online
        //testAssembly.goOnline()
    }

    onAbortSequence {
        //do some actions to abort sequence

        //send abortSequence command to downstream sequencer
        //lgsfSequencer.abortSequence()
    }

    onStop {
        //do some actions to stop

        //send stop command to downstream sequencer
        //lgsfSequencer.stop()
    }

}
